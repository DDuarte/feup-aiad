package pt.up.fe.aiad.scheduler;

import jade.lang.acl.ACLMessage;

public class Message {

    public enum Type {
        IsOkay,
        NoGood,
        AddLink
    }

    public static final int[] ACL = {
        ACLMessage.PROPOSE,
        ACLMessage.REJECT_PROPOSAL,
        ACLMessage.SUBSCRIBE
    };
}
