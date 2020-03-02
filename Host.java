package mkf.jade.guessinggame;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.Agent;
import jade.core.AID;

public class Host extends EnhancedAgent {
  private int period = Constants.PERIOD;
  public Set<AID> players = new HashSet<>();

  protected void setup() {
    System.out.println("My name is " + getLocalName());
    register("Host");
    addBehaviour(new TickerBehaviour (this, period) {
      protected void onTick() {
        players = searchForService(Constants.PLAYER_SERVICE_NAME);
        System.out.printf("Number of players so far: %d%n", players.size());
        if (players.size() >= 2) {
            stop();
            restOfTheBehaviours();
        }
      }
    });
  }

  private void restOfTheBehaviours() {
    System.out.printf("We're good to go with %d players%n", players.size());
    addBehaviour(new HostBehaviour(this));
  }
}

class HostBehaviour extends SimpleBehaviour {
  private boolean done = false;
  private int goal;
  private Random rand;
  private short actionCounter = 0;
  private Host myAgent;
  private Set<AID> respondingPlayers = new HashSet<>();
  private static final short MIN_LIMIT = 2;
  private static final int MAX_LIMIT = 60;

  public HostBehaviour(Agent agent) {
      super(agent);
      myAgent = (Host) agent;
      rand = new Random();
  }

  private void sendMsg(String content, String conversationId, int type, Set<AID> receivers) {
    ACLMessage msg = new ACLMessage(type);
    msg.setContent(content);
    msg.setConversationId(conversationId);
    //add receivers
    for (AID agent: receivers) {
      msg.addReceiver(agent);
    }
    myAgent.send(msg);
  }

  private void generateRandomNumber() {
    goal = rand.nextInt(Constants.MAX_VALUE);
  }

  public void action() {
    ACLMessage msg;
    MessageTemplate template;

    switch(actionCounter) {
      case 0:
        sendMsg("Wanna Play?", Constants.PLAY_REQ, ACLMessage.REQUEST, myAgent.players);
        actionCounter++;
      break;

      case 1:
        //listening for replies
        System.out.println("Waiting for response ...");
        template = MessageTemplate.and(
                  MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                  MessageTemplate.MatchConversationId(Constants.PLAY_REQ));
        msg = myAgent.blockingReceive(template);
        if(msg != null) {
          //just received an accept
          respondingPlayers.add(msg.getSender());
          System.out.printf("Agent %s just accepted the proposal%n", msg.getSender().getLocalName());
          if(respondingPlayers.size() == myAgent.players.size()) {
            //everybody has accepted
            //we can play
            actionCounter = 2;
          }
        }
      break;

      case 2:
        System.out.println("Everybody has agreed to play. Let's go!");
        generateRandomNumber();
        sendMsg("Guess", Constants.GUESS, ACLMessage.REQUEST, myAgent.players);
        actionCounter = 3;
      break;

      case 3:
        template = MessageTemplate.and(
                  MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF),
                  MessageTemplate.MatchConversationId(Constants.GUESS));
        msg = myAgent.blockingReceive(template);
        if(msg != null) {
          //we just received a guess
          System.out.printf("Agent %s guessed number %s%n", msg.getSender().getLocalName(), msg.getContent());
          if(goal == Integer.parseInt(msg.getContent())) {
            System.out.println("==============================================================");
            System.out.printf("Agent %s guessed correctly and the goal was %d! Game is over!%n%n", msg.getSender().getLocalName(), goal);
            sendMsg("Game's over", Constants.OVER, ACLMessage.INFORM, myAgent.players);
            done = true;
            myAgent.doDelete();
          }

          else {
            //guess is incorrect
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.REQUEST);
            reply.setContent("Guess");
            reply.setConversationId(Constants.GUESS);
            myAgent.send(reply);
          }
        }
      break;
    }
  }

  public boolean done() {
    return done;
  }

}
