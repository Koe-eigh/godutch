package com.godutch.payment;

import com.godutch.group.GroupId;
import java.util.List;
import java.util.Optional;

/**
 * 支払いイベントのリポジトリインターフェース
 */
public interface PaymentEventRepository {
    
    /**
     * 支払いイベントを保存します
     * 
     * @param paymentEvent 保存する支払いイベント
     * @return 保存された支払いイベント
     */
    PaymentEvent save(PaymentEvent paymentEvent);
    
    /**
     * 支払いイベントをIDで検索します（PaymentEventId版）
     * 
     * @param id 検索する支払いイベントのID
     * @return 検索された支払いイベント、存在しない場合はOptional.empty()
     */
    Optional<PaymentEvent> findById(PaymentEventId id);
    
    /**
     * すべての支払いイベントを取得します
     * 
     * @return 支払いイベント一覧
     */
    List<PaymentEvent> findAll();

    Optional<List<PaymentEvent>> findAllBy(GroupId groupId);
    
    /**
     * 支払いイベントを削除します（PaymentEventId版）
     * 
     * @param id 削除する支払いイベントのID
     */
    void deleteById(PaymentEventId id);
    
    /**
     * 支払いイベントが存在するかチェックします（PaymentEventId版）
     * 
     * @param id 支払いイベントのID
     * @return 存在する場合true
     */
    boolean exists(PaymentEventId id);
}
