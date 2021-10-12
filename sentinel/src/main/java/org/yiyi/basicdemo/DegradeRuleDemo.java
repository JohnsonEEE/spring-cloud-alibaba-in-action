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
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yi.yi
 * @date 2021.09.29
 */
public class DegradeRuleDemo {
    private static final String RESOURCE_NAME = "degrade_resource";
    public static SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

    public static void main (String[] args) throws InterruptedException {
        initRules ();

        for (;;) {
            slowCall ();
            Thread.sleep (100L);
        }
    }

    private static void initRules () {
        List <DegradeRule> rules = new ArrayList <> ();
//        DegradeRule errorRatioRule = new DegradeRule (RESOURCE_NAME);
//        errorRatioRule.setGrade (CircuitBreakerStrategy.ERROR_RATIO.getType ());
//        errorRatioRule.setCount (0.5);
//        errorRatioRule.setMinRequestAmount (5)
//                .setStatIntervalMs (2000)
//                .setTimeWindow (10);
//        rules.add (errorRatioRule);

        DegradeRule slowRatioRule = new DegradeRule (RESOURCE_NAME);
        slowRatioRule.setGrade (CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType ())
                    .setCount (1001)
                    .setSlowRatioThreshold (0.5)
                    .setTimeWindow (5)
                    .setStatIntervalMs (10000);
        rules.add (slowRatioRule);
        DegradeRuleManager.loadRules (rules);
    }

    private static void errorCall () {
        Entry entry = null;
        try {
            entry = SphU.entry (RESOURCE_NAME);
            System.out.println ("into the call");
            String a = null;
            a.split (",");
        }
        catch (BlockException e) {
            System.out.println (sdf.format (new Date ()) + ": block " + e.getMessage ());
        }
        catch (Throwable e) {
            System.out.println (sdf.format (new Date ()) + ": business error " + e.getMessage ());
            Tracer.trace (e);
        }
        finally {
            if (entry != null) {
                entry.exit ();
            }
        }
    }

    private static void slowCall () {
        Entry entry = null;
        try {
            entry = SphU.entry (RESOURCE_NAME);
            System.out.println ("into the call");
            Thread.sleep (1100L);
        }
        catch (BlockException e) {
            System.out.println (sdf.format (new Date ()) + ": block " + e.getMessage ());
        }
        catch (Throwable e) {
            System.out.println (sdf.format (new Date ()) + ": business error " + e.getMessage ());
            Tracer.trace (e);
        }
        finally {
            if (entry != null) {
                entry.exit ();
            }
        }
    }
}
