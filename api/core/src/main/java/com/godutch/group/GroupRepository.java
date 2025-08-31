package com.godutch.group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    /**
     * グループを保存します。
     *
     * @param group 保存するグループ
     * @return 保存されたグループ
     */
    Group save(Group group);

    /**
     * グループをIDで検索します。
     *
     * @param id 検索するグループのID
     * @return 検索されたグループ、存在しない場合はOptional.empty()
     */
    Optional<Group> findById(GroupId id);
    
    /**
     * グループの存在を確認します。
     *
     * @param id 確認するグループのID
     * @return 存在する場合はtrue、存在しない場合はfalse
     */
    boolean exists(GroupId id);
    
    /**
     * すべてのグループを取得します。
     *
     * @return すべてのグループのリスト
     */
    List<Group> findAll();
}
