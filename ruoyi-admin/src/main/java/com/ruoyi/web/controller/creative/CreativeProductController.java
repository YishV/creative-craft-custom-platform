package com.ruoyi.web.controller.creative;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.service.creative.ICreativeProductService;
import java.util.Map;

@RestController
@RequestMapping("/creative/product")
public class CreativeProductController extends BaseController
{
    @Autowired
    private ICreativeProductService creativeProductService;

    @PreAuthorize("@ss.hasPermi('creative:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(CreativeProduct creativeProduct)
    {
        startPage();
        List<CreativeProduct> list = creativeProductService.selectCreativeProductList(creativeProduct);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('creative:product:query')")
    @GetMapping("/{productId}")
    public AjaxResult getInfo(@PathVariable Long productId)
    {
        return success(creativeProductService.selectCreativeProductByProductId(productId));
    }

    @PreAuthorize("@ss.hasPermi('creative:product:add')")
    @Log(title = "手作商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CreativeProduct creativeProduct)
    {
        if (!SecurityUtils.isAdmin())
        {
            creativeProduct.setCreatorId(null);
        }
        creativeProduct.setCreateBy(getUsername());
        return toAjax(creativeProductService.insertCreativeProduct(creativeProduct));
    }

    @PreAuthorize("@ss.hasPermi('creative:product:edit')")
    @Log(title = "手作商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CreativeProduct creativeProduct)
    {
        creativeProduct.setUpdateBy(getUsername());
        return toAjax(creativeProductService.updateCreativeProduct(creativeProduct));
    }

    @PreAuthorize("@ss.hasPermi('creative:product:edit')")
    @Log(title = "手作商品", businessType = BusinessType.UPDATE)
    @PostMapping("/{productId}/putOnShelf")
    public AjaxResult putOnShelf(@PathVariable Long productId)
    {
        return toAjax(creativeProductService.putOnShelf(productId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('creative:product:edit')")
    @Log(title = "手作商品", businessType = BusinessType.UPDATE)
    @PostMapping("/{productId}/takeOffShelf")
    public AjaxResult takeOffShelf(@PathVariable Long productId)
    {
        return toAjax(creativeProductService.takeOffShelf(productId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('creative:product:edit')")
    @Log(title = "手作商品", businessType = BusinessType.UPDATE)
    @PostMapping("/{productId}/submitAudit")
    public AjaxResult submitAudit(@PathVariable Long productId)
    {
        return toAjax(creativeProductService.submitAudit(productId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('creative:product:audit')")
    @Log(title = "手作商品", businessType = BusinessType.UPDATE)
    @PostMapping("/{productId}/approveAudit")
    public AjaxResult approveAudit(@PathVariable Long productId, @RequestBody(required = false) Map<String, String> body)
    {
        String remark = body == null ? null : body.get("remark");
        return toAjax(creativeProductService.approveAudit(productId, remark, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('creative:product:audit')")
    @Log(title = "手作商品", businessType = BusinessType.UPDATE)
    @PostMapping("/{productId}/rejectAudit")
    public AjaxResult rejectAudit(@PathVariable Long productId, @RequestBody Map<String, String> body)
    {
        String remark = body == null ? null : body.get("remark");
        return toAjax(creativeProductService.rejectAudit(productId, remark, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('creative:product:remove')")
    @Log(title = "手作商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        return toAjax(creativeProductService.deleteCreativeProductByProductIds(productIds));
    }
}
