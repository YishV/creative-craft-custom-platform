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
import com.ruoyi.system.domain.creative.CreativeOrder;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.service.creative.ICreativeOrderService;

@RestController
@RequestMapping("/creative/order")
public class CreativeOrderController extends BaseController
{
    @Autowired
    private ICreativeOrderService creativeOrderService;

    @PreAuthorize("@ss.hasPermi('creative:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(CreativeOrder creativeOrder)
    {
        startPage();
        List<CreativeOrder> list = creativeOrderService.selectCreativeOrderList(creativeOrder);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('creative:order:query')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(creativeOrderService.selectCreativeOrderByOrderId(orderId));
    }

    @PreAuthorize("@ss.hasPermi('creative:order:add')")
    @Log(title = "定制订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CreativeOrder creativeOrder)
    {
        creativeOrder.setCreateBy(getUsername());
        return toAjax(creativeOrderService.insertCreativeOrder(creativeOrder));
    }

    @PreAuthorize("@ss.hasPermi('creative:order:edit')")
    @Log(title = "定制订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CreativeOrder creativeOrder)
    {
        creativeOrder.setUpdateBy(getUsername());
        return toAjax(creativeOrderService.updateCreativeOrder(creativeOrder));
    }

    @PreAuthorize("@ss.hasPermi('creative:order:remove')")
    @Log(title = "定制订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(creativeOrderService.deleteCreativeOrderByOrderIds(orderIds));
    }

    /** 开始制作：created -> making */
    @PreAuthorize("@ss.hasPermi('creative:order:edit')")
    @Log(title = "订单-开始制作", businessType = BusinessType.UPDATE)
    @PostMapping("/start/{orderId}")
    public AjaxResult startMaking(@PathVariable Long orderId)
    {
        return toAjax(creativeOrderService.transitOrderStatus(orderId, CreativeStatusFlow.Order.MAKING, getUsername()));
    }

    /** 发货：making -> shipped */
    @PreAuthorize("@ss.hasPermi('creative:order:edit')")
    @Log(title = "订单-发货", businessType = BusinessType.UPDATE)
    @PostMapping("/ship/{orderId}")
    public AjaxResult ship(@PathVariable Long orderId)
    {
        return toAjax(creativeOrderService.transitOrderStatus(orderId, CreativeStatusFlow.Order.SHIPPED, getUsername()));
    }

    /** 完成：shipped -> finished */
    @PreAuthorize("@ss.hasPermi('creative:order:edit')")
    @Log(title = "订单-完成", businessType = BusinessType.UPDATE)
    @PostMapping("/finish/{orderId}")
    public AjaxResult finish(@PathVariable Long orderId)
    {
        return toAjax(creativeOrderService.transitOrderStatus(orderId, CreativeStatusFlow.Order.FINISHED, getUsername()));
    }

    /** 取消：created/making -> cancelled */
    @PreAuthorize("@ss.hasPermi('creative:order:edit')")
    @Log(title = "订单-取消", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel/{orderId}")
    public AjaxResult cancel(@PathVariable Long orderId)
    {
        return toAjax(creativeOrderService.transitOrderStatus(orderId, CreativeStatusFlow.Order.CANCELLED, getUsername()));
    }
}
