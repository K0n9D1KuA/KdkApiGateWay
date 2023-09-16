package com.qyh.gateway.config.center.api;



import config.Rule;

import java.util.List;

public interface RulesChangeListener {
    void onRulesChange(List<Rule> rules);
}
