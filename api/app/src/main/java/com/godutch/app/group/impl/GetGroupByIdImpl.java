package com.godutch.app.group.impl;

import com.godutch.app.group.api.GetGroupById;
import com.godutch.app.group.api.GetGroupByIdInputPort;
import com.godutch.app.group.api.GetGroupByIdOutputPort;
import com.godutch.group.GroupRepository;
import com.godutch.payment.PaymentEventRepository;

public class GetGroupByIdImpl implements GetGroupById {

    private final GroupRepository groupRepository;
    private final PaymentEventRepository paymentEventRepository;

    public GetGroupByIdImpl(
            GroupRepository groupRepository,
            PaymentEventRepository paymentEventRepository) {
        this.groupRepository = groupRepository;
        this.paymentEventRepository = paymentEventRepository;
    }

    @Override
    public void execute(GetGroupByIdInputPort input, GetGroupByIdOutputPort output) {
       this.groupRepository.findById(input.groupId())
            .ifPresentOrElse(
                group -> output.result(
                    group,
                    paymentEventRepository.findTotalPaidAmountBy(input.groupId())
                ),
                () -> output.result(null, null)
            );
    }
    
}
