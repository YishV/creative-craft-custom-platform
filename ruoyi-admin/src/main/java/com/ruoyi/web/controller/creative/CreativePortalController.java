package com.ruoyi.web.controller.creative;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.creative.CreativeComment;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.domain.creative.CreativeDemand;
import com.ruoyi.system.domain.creative.CreativeFavorite;
import com.ruoyi.system.domain.creative.CreativeOrder;
import com.ruoyi.system.domain.creative.CreativePost;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.domain.creative.CreativeProductOrderRequest;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeCommentMapper;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.mapper.creative.CreativeFavoriteMapper;
import com.ruoyi.system.mapper.creative.CreativePostMapper;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
import com.ruoyi.system.service.creative.ICreativeOrderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal")
public class CreativePortalController extends BaseController
{
    private static final String STATUS_ON_SHELF = "0";

    @Autowired
    private CreativeProductMapper creativeProductMapper;

    @Autowired
    private CreativeDemandMapper creativeDemandMapper;

    @Autowired
    private CreativeCreatorMapper creativeCreatorMapper;

    @Autowired
    private CreativePostMapper creativePostMapper;

    @Autowired
    private CreativeCommentMapper creativeCommentMapper;

    @Autowired
    private CreativeFavoriteMapper creativeFavoriteMapper;

    @Autowired
    private ICreativeOrderService creativeOrderService;

    @GetMapping("/product/list")
    public TableDataInfo listProducts(CreativeProduct product)
    {
        product.setStatus(STATUS_ON_SHELF);
        startPage();
        List<CreativeProduct> list = creativeProductMapper.selectCreativeProductList(product);
        return getDataTable(list);
    }

    @GetMapping("/product/{productId}")
    public AjaxResult getProduct(@PathVariable Long productId)
    {
        CreativeProduct product = creativeProductMapper.selectCreativeProductByProductId(productId);
        if (product == null || !STATUS_ON_SHELF.equals(product.getStatus()))
        {
            throw new ServiceException("商品不存在或已下架");
        }
        return success(product);
    }

    @GetMapping("/demand/list")
    public TableDataInfo listDemands(CreativeDemand demand)
    {
        if (demand.getDemandStatus() == null || demand.getDemandStatus().isEmpty())
        {
            demand.setDemandStatus(CreativeStatusFlow.Demand.PUBLISHED);
        }
        startPage();
        List<CreativeDemand> list = creativeDemandMapper.selectCreativeDemandList(demand);
        return getDataTable(list);
    }

    @GetMapping("/demand/{demandId}")
    public AjaxResult getDemand(@PathVariable Long demandId)
    {
        CreativeDemand demand = creativeDemandMapper.selectCreativeDemandByDemandId(demandId);
        if (demand == null)
        {
            throw new ServiceException("需求不存在");
        }
        return success(demand);
    }

    @PostMapping("/order/product")
    public AjaxResult createProductOrder(@RequestBody CreativeProductOrderRequest request)
    {
        CreativeOrder order = creativeOrderService.createProductOrder(request, getUsername());
        return success(order);
    }

    @PostMapping("/order/pay/{orderId}")
    public AjaxResult payOrder(@PathVariable Long orderId)
    {
        return toAjax(creativeOrderService.payOrder(orderId, getUsername()));
    }

    @GetMapping("/creator/list")
    public TableDataInfo listCreators(CreativeCreator creator)
    {
        creator.setStatus(STATUS_ON_SHELF);
        creator.setAuditStatus("approved");
        startPage();
        List<CreativeCreator> list = creativeCreatorMapper.selectCreativeCreatorList(creator);
        return getDataTable(list);
    }

    @GetMapping("/creator/{creatorId}")
    public AjaxResult getCreator(@PathVariable Long creatorId)
    {
        CreativeCreator creator = creativeCreatorMapper.selectCreativeCreatorByCreatorId(creatorId);
        if (creator == null || !STATUS_ON_SHELF.equals(creator.getStatus()) || !"approved".equals(creator.getAuditStatus()))
        {
            throw new ServiceException("创作者不存在或未通过审核");
        }
        return success(creator);
    }

    @GetMapping("/post/list")
    public TableDataInfo listPosts(CreativePost post)
    {
        post.setStatus(STATUS_ON_SHELF);
        startPage();
        List<CreativePost> list = creativePostMapper.selectCreativePostList(post);
        return getDataTable(list);
    }

    @GetMapping("/post/{postId}")
    public AjaxResult getPost(@PathVariable Long postId)
    {
        CreativePost post = creativePostMapper.selectCreativePostByPostId(postId);
        if (post == null || !STATUS_ON_SHELF.equals(post.getStatus()))
        {
            throw new ServiceException("作品不存在或已下架");
        }
        return success(post);
    }

    @GetMapping("/comment/list")
    public TableDataInfo listComments(CreativeComment comment)
    {
        comment.setAuditStatus("approved");
        startPage();
        List<CreativeComment> list = creativeCommentMapper.selectCreativeCommentList(comment);
        return getDataTable(list);
    }

    @PostMapping("/comment")
    public AjaxResult addComment(@RequestBody CreativeComment comment)
    {
        CreativePost post = creativePostMapper.selectCreativePostByPostId(comment.getPostId());
        if (post == null || !STATUS_ON_SHELF.equals(post.getStatus()))
        {
            throw new ServiceException("作品不存在或已下架");
        }
        comment.setUserId(SecurityUtils.getUserId());
        comment.setAuditStatus("approved");
        comment.setCreateBy(getUsername());
        return toAjax(creativeCommentMapper.insertCreativeComment(comment));
    }

    @GetMapping("/favorite/list")
    public TableDataInfo listFavorites(CreativeFavorite favorite)
    {
        favorite.setUserId(SecurityUtils.getUserId());
        startPage();
        List<CreativeFavorite> list = creativeFavoriteMapper.selectCreativeFavoriteList(favorite);
        return getDataTable(list);
    }

    @PostMapping("/favorite")
    public AjaxResult addFavorite(@RequestBody CreativeFavorite favorite)
    {
        favorite.setUserId(SecurityUtils.getUserId());
        favorite.setStatus(STATUS_ON_SHELF);
        favorite.setCreateBy(getUsername());
        return toAjax(creativeFavoriteMapper.insertCreativeFavorite(favorite));
    }

    @PostMapping("/favorite/{favoriteId}/cancel")
    public AjaxResult cancelFavorite(@PathVariable Long favoriteId)
    {
        CreativeFavorite favorite = creativeFavoriteMapper.selectCreativeFavoriteByFavoriteId(favoriteId);
        if (favorite == null || !SecurityUtils.getUserId().equals(favorite.getUserId()))
        {
            throw new ServiceException("收藏记录不存在");
        }
        return toAjax(creativeFavoriteMapper.deleteCreativeFavoriteByFavoriteId(favoriteId));
    }
}
