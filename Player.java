package mkf.jade.guessinggame;

import java.util.Random;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.AID;
import jade.core.Agent;

public class Player extends EnhancedAgent {
    protected void setup() {
      System.out.printf("Hello! My name is %s%n", getLocalName());
      register(Constants.PLAYER_SERVICE_NAME);
      addBehaviour(new PlayerBehaviour());
    }

    private class PlayerBehaviour extends SimpleBehaviour {
      private Player myAgent;
      private boolean done = false;
      private Random rand;
      private short actionCounter = 0;

      public PlayerBehaviour() {
        super(Player.this);
        myAgent = Player.this;
        rand = new Random();
      }

      public void action() {
        ACLMessage msg, reply;
        MessageTemplate template;

        switch(actionCounter) {
          case 0:
            //listening for game REQUEST
            template = MessageTemplate.and(
                      MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                      MessageTemplate.MatchConversationId(Constants.PLAY_REQ));
            msg = myAgent.blockingReceive(template);
            if(msg != null) {
              System.out.println("I, " + getLocalName() + ", received a play request");
              //reply with accepted
              reply = msg.createReply();
              reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
              reply.setConversationId(msg.getConversationId());
              myAgent.send(reply);
              actionCounter = 1;
            }
          break;

          case 1:
            msg = myAgent.blockingReceive();
            if(msg != null) {
              if(msg.getPerformative() == ACLMessage.REQUEST && msg.getConversationId().equals(Constants.GUESS)) {
                  reply = msg.createReply();
                  reply.setContent(guess());
                  reply.setPerformative(ACLMessage.QUERY_IF);
                  reply.setConversationId(msg.getConversationId());
                  myAgent.send(reply);
              }

              else if(msg.getPerformative() == ACLMessage.INFORM && msg.getConversationId().equals(Constants.OVER)) {
                  done = true;
                  myAgent.doDelete();
              }
            }
          break;
        }
      }

      private String guess() {
        return Integer.toString(rand.nextInt(Constants.MAX_VALUE));
      }

      public boolean done() {
        return done;
    }
  }
}
