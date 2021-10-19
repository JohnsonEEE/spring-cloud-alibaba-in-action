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
package seata.filter;

import io.seata.core.context.RootContext;
import org.apache.commons.lang.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 所有http请求先经过此filter，若请求头中有seata xid，则取出来bind到RootContext中
 *
 * @author yi.yi
 * @date 2021.10.15
 */
public class SeataFilter implements Filter {

    @Override
    public void init (FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter (ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String xid = req.getHeader (RootContext.KEY_XID.toLowerCase ());
        boolean isBind = false;
        if (StringUtils.isNotBlank (xid)) {
            RootContext.bind (xid);
            isBind = true;
        }
        try {
            filterChain.doFilter (servletRequest, servletResponse);
        }
        finally {
            if (isBind) {
                RootContext.unbind ();
            }
        }
    }

    @Override
    public void destroy () {

    }
}
