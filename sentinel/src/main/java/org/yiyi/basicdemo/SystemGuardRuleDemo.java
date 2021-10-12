/*
 *
 *
 * Copyright ( c ) 2021 TH Supcom Corporation. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TH Supcom
 * Corporation ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with TH Supcom Corporation or a TH Supcom
 * authorized reseller (the "License Agreement"). TH Supcom may make changes to the
 * Confidential Information from time to time. Such Confidential Information may
 * contain errors.
 *
 * EXCEPT AS EXPLICITLY SET FORTH IN THE LICENSE AGREEMENT, TH Supcom DISCLAIMS ALL
 * WARRANTIES, COVENANTS, REPRESENTATIONS, INDEMNITIES, AND GUARANTEES WITH
 * RESPECT TO SOFTWARE AND DOCUMENTATION, WHETHER EXPRESS OR IMPLIED, WRITTEN OR
 * ORAL, STATUTORY OR OTHERWISE INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, TITLE, NON-INFRINGEMENT AND FITNESS FOR A
 * PARTICULAR PURPOSE. TH Supcom DOES NOT WARRANT THAT END USER'S USE OF THE
 * SOFTWARE WILL BE UNINTERRUPTED, ERROR FREE OR SECURE.
 *
 * TH Supcom SHALL NOT BE LIABLE TO END USER, OR ANY OTHER PERSON, CORPORATION OR
 * ENTITY FOR INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR CONSEQUENTIAL
 * DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR USE, WHETHER IN AN
 * ACTION IN CONTRACT, TORT OR OTHERWISE, EVEN IF TH Supcom HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. TH Supcom' TOTAL LIABILITY TO END USER SHALL NOT
 * EXCEED THE AMOUNTS PAID FOR THE TH Supcom SOFTWARE BY END USER DURING THE PRIOR
 * TWELVE (12) MONTHS FROM THE DATE IN WHICH THE CLAIM AROSE.  BECAUSE SOME
 * STATES OR JURISDICTIONS DO NOT ALLOW LIMITATION OR EXCLUSION OF CONSEQUENTIAL
 * OR INCIDENTAL DAMAGES, THE ABOVE LIMITATION MAY NOT APPLY TO END USER.
 *
 * Copyright version 2.0
 */
package org.yiyi.basicdemo;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

/**
 * @author yi.yi
 * @date 2021.09.29
 */
public class SystemGuardRuleDemo {
    public static SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

    public static void main (String[] args) {
        initRules ();

        for (int i = 1; i <= 3; i++) {
            final int fi = i;
            new Thread (() -> {
                while (true) {
                    callResource (fi);
                    try {
                        Thread.sleep (200L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace ();
                    }
                }
            }).start ();
        }
    }

    private static void callResource (int fi) {
        String currentTime = sdf.format (new Date ());
        Entry entry = null;
        try {
            entry = SphU.entry ("sys_resource_" + fi, EntryType.IN);
            System.out.println (currentTime + ": " + fi + " into the call");
        }
        catch (BlockException e) {
            System.out.println (currentTime + ": " + fi + " resouce blocked " + e.getMessage ());
        }
        catch (Throwable e) {
            System.out.println (currentTime + ": " + fi + " business error " + e.getMessage ());
            Tracer.trace (e);
        }
        finally {
            if (entry != null) {
                entry.exit ();
            }
        }
    }

    private static void initRules () {
        SystemRule rule = new SystemRule();
        rule.setHighestSystemLoad(3.0);
        rule.setHighestCpuUsage(0.6);
        rule.setAvgRt(10);
        rule.setQps(10);
        rule.setMaxThread(5);

        SystemRuleManager.loadRules(Collections.singletonList(rule));
    }
}
