package com.godutch.web.config;

import com.godutch.app.group.api.CreateGroup;
import com.godutch.app.group.api.GetGroupById;
import com.godutch.app.group.impl.CreateGroupImpl;
import com.godutch.app.group.impl.GetGroupByIdImpl;
import com.godutch.app.payment.api.AddPaymentEvent;
import com.godutch.app.payment.api.DeletePaymentEvent;
import com.godutch.app.payment.api.GetAllPaymentEventsByGroupId;
import com.godutch.app.payment.api.GetPaymentEventById;
import com.godutch.app.payment.api.GetWholeSettlements;
import com.godutch.app.payment.api.UpdatePaymentEvent;
import com.godutch.app.payment.impl.AddPaymentEventImpl;
import com.godutch.app.payment.impl.DeletePaymentEventImpl;
import com.godutch.app.payment.impl.GetAllPaymentEventsByGroupIdImpl;
import com.godutch.app.payment.impl.GetPaymentEventImpl;
import com.godutch.app.payment.impl.GetWholesettlementsImpl;
import com.godutch.app.payment.impl.UpdatePaymentEventImpl;
import com.godutch.group.GroupRepository;
import com.godutch.payment.PaymentEventRepository;
import com.godutch.payment.PaymentEventService;
import com.godutch.payment.PaymentEventServiceSimpleImpl;
import com.godutch.database.repository.JdbcGroupRepository;
import com.godutch.database.repository.JdbcPaymentEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean
    public GroupRepository groupRepository(DataSource dataSource) {
        return new JdbcGroupRepository(dataSource);
    }

    @Bean
    public PaymentEventRepository paymentEventRepository(DataSource dataSource) {
        return new JdbcPaymentEventRepository(dataSource);
    }

    @Bean
    public CreateGroup createGroup(GroupRepository groupRepository) {
        return new CreateGroupImpl(groupRepository);
    }

    @Bean
    public GetGroupById getGroupById(GroupRepository groupRepository) {
        return new GetGroupByIdImpl(groupRepository);
    }

    @Bean
    public AddPaymentEvent addPaymentEvent(PaymentEventRepository paymentEventRepository, GroupRepository groupRepository) {
        return new AddPaymentEventImpl(paymentEventRepository);
    }

    @Bean
    public GetAllPaymentEventsByGroupId getAllPaymentEventsByGroupId(PaymentEventRepository paymentEventRepository) {
        return new GetAllPaymentEventsByGroupIdImpl(paymentEventRepository);
    }

    @Bean
    public GetPaymentEventById getPaymentEventById(PaymentEventRepository paymentEventRepository) {
        return new GetPaymentEventImpl(paymentEventRepository);
    }

    @Bean
    public UpdatePaymentEvent updatePaymentEvent(PaymentEventRepository paymentEventRepository) {
        return new UpdatePaymentEventImpl(paymentEventRepository);
    }

    @Bean
    public DeletePaymentEvent deletePaymentEvent(PaymentEventRepository paymentEventRepository) {
        return new DeletePaymentEventImpl(paymentEventRepository);
    }

    @Bean
    public GetWholeSettlements getWholeSettlements(PaymentEventRepository paymentEventRepository, PaymentEventService paymentEventService) {
        return new GetWholesettlementsImpl(paymentEventRepository, paymentEventService);
    }

    @Bean
    public PaymentEventService paymentEventService() {
        return new PaymentEventServiceSimpleImpl();
    }
}
