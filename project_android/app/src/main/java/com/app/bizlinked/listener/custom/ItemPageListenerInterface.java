package com.app.bizlinked.listener.custom;

import com.app.bizlinked.models.GenericNameIdModal;

import java.util.List;

public interface ItemPageListenerInterface {
    void itemPageListenerInterfaceCallBack(List<GenericNameIdModal> items, boolean isSingleSelect);
}
