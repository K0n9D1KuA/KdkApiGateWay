package com.qyh.gateway.config.center.api;



import config.RuleNew;

import java.util.List;

public interface NewRulesChangeListener {
    void onRulesChange(List<RuleNew> rules);
}
