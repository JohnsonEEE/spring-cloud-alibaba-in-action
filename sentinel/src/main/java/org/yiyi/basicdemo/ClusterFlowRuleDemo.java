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
import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.cluster.registry.ConfigSupplierRegistry;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.ClusterRuleConstant;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.ClusterFlowConfig;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.yiyi.basicdemo.cluster.ClusterStandaloneServerDemo;
import org.yiyi.basicdemo.cluster.SimpleLocalDataSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yi.yi
 * @date 2021.09.30
 */
public class ClusterFlowRuleDemo {
    public static String RESOURCE = "hello_cluster_resource";
    public static SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

    public static void main (String[] args) throws InterruptedException {
        initRules ();

        for (; ; ) {
            callResource ();
            Thread.sleep (100L);
        }
    }

    private static void initRules () {
        ClusterStateManager.applyState (ClusterStateManager.CLUSTER_CLIENT);
        ClusterClientConfig clientConfig = new ClusterClientConfig ();
        clientConfig.setRequestTimeout (1000);
        ClusterClientConfigManager.applyNewConfig (clientConfig);

        SimpleLocalDataSource ruleSource = new SimpleLocalDataSource (ClusterStandaloneServerDemo.CLUSTER_SERVER_NAMESPACE);
        FlowRuleManager.register2Property (ruleSource.getProperty ());

        ClusterClientAssignConfig assignConfig = new ClusterClientAssignConfig ();
        assignConfig.setServerHost ("127.0.0.1");
        assignConfig.setServerPort (11111);
        // 先指定名称空间为 serviceA
        ConfigSupplierRegistry.setNamespaceSupplier (() -> ClusterStandaloneServerDemo.CLUSTER_SERVER_NAMESPACE);
        ClusterClientConfigManager.applyNewAssignConfig (assignConfig);
    }

    private static void callResource () {
        FlowRuleDemo d = new FlowRuleDemo ();
        try (Entry entry = SphU.entry (RESOURCE)) {
            d.sayHello ();
        }
        catch (BlockException be) {
            d.fallback ();
        }
        catch (Throwable e) {
            e.printStackTrace ();
        }
    }

    public void sayHello () {
        System.out.println (sdf.format (new Date ()) + ": hello world!");
    }

    public void fallback () {
        System.out.println ("woops!!!!!!");
    }
}
