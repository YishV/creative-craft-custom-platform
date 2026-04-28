package com.ruoyi.web.controller.creative;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.creative.CreativeChatMessage;
import com.ruoyi.system.domain.creative.CreativeChatMessageRequest;
import com.ruoyi.system.domain.creative.CreativeChatSession;
import com.ruoyi.system.domain.creative.CreativeChatSessionRequest;
import com.ruoyi.system.service.creative.ICreativeChatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/chat")
public class CreativeChatController extends BaseController
{
    @Autowired
    private ICreativeChatService creativeChatService;

    @GetMapping("/session/list")
    public TableDataInfo listSessions()
    {
        startPage();
        List<CreativeChatSession> list = creativeChatService.listMySessions(getUserId());
        return getDataTable(list);
    }

    @PostMapping("/session")
    public AjaxResult openSession(@RequestBody CreativeChatSessionRequest request)
    {
        return success(creativeChatService.openSession(request, getUserId(), getUsername()));
    }

    @GetMapping("/message/list")
    public AjaxResult listMessages(@RequestParam Long sessionId)
    {
        List<CreativeChatMessage> list = creativeChatService.listMessages(sessionId, getUserId());
        return success(list);
    }

    @PostMapping("/message")
    public AjaxResult sendMessage(@RequestBody CreativeChatMessageRequest request)
    {
        return success(creativeChatService.sendMessage(request, getUserId(), getUsername()));
    }

    @PostMapping("/session/{id}/read")
    public AjaxResult markRead(@PathVariable Long id)
    {
        return toAjax(creativeChatService.markRead(id, getUserId()));
    }
}
