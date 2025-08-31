package com.godutch.web.group.controller;

import com.godutch.app.group.api.CreateGroup;
import com.godutch.app.group.api.GetGroupById;
import com.godutch.web.group.dto.CreateGroupRequest;
import com.godutch.web.group.dto.GroupResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@Validated
public class GroupController {
    private final CreateGroup createGroup;
    private final GetGroupById getGroupById;

    public GroupController(
        CreateGroup createGroup,
        GetGroupById getGroupById
    ) {
        this.createGroup = createGroup;
        this.getGroupById = getGroupById;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> create(@RequestBody CreateGroupRequest request) {
        CreateGroupHttpRequestHandler input = new CreateGroupHttpRequestHandler(request);
        CreateGroupHttpResponsePresenter output = new CreateGroupHttpResponsePresenter();
        createGroup.execute(input, output);
        return output.present();
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> get(@PathVariable String groupId) {
        GetGroupByIdHttpRequestHandler input = new GetGroupByIdHttpRequestHandler(groupId);
        GetGroupByIdHttpResponsePresenter output = new GetGroupByIdHttpResponsePresenter();
        getGroupById.execute(input, output);
        return output.present();
    }
}
