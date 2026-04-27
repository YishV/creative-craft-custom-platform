package com.ruoyi.web.controller.creative;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeDemand;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
