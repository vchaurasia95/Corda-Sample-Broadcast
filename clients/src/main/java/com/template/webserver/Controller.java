package com.template.webserver;

import com.template.flows.IOUFlow;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import org.checkerframework.checker.units.qual.Time;
import org.hibernate.internal.build.AllowSysOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/templateendpoint", produces = "text/plain")
    private String templateendpoint() {
        return "Define an endpoint here.";
    }

    @PostMapping(value = "/createIOU", produces= "application/json")
    private String createIOU(@RequestBody IOUDetails details) throws Exception {
//        System.out.println(details.getOtherParty()+", "+details.getPartyToShare()+", "+details.getValue());
        Party other = proxy.partiesFromName(details.getOtherParty(),false).iterator().next();
        Party partyToBroadcast = proxy.partiesFromName(details.getPartyToShare(),false).iterator().next();
       // System.out.println(other);
       // System.out.println(partyToBroadcast);
        String result = null;
        try {

           SignedTransaction res = proxy.startFlowDynamic(IOUFlow.class,details.getValue(),other,partyToBroadcast).getReturnValue()
                   .get(5000, TimeUnit.MILLISECONDS);
           if(res != null) {
               result = "Transaction Completed Successfully!! Tx Id#" + res.getId();
           }
        }catch (TimeoutException exception){
            System.out.println("Exception during creation of IOU"+ exception);
            result = "Transaction Unable to be Completed in 5s !! Maybe some nodes are down check logs";
        }
        return result;

    }
}