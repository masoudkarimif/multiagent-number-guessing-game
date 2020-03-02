package mkf.jade.guessinggame;

import jade.core.Agent;
import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class CreatePlayers extends Agent {
  protected void setup() {
        Object[] args = getArguments();
      int numberOfPlayers = Integer.parseInt( (String) args[0]);
      for (int i=0; i<numberOfPlayers; i++) {
      	String name = "Player" + (i+1);
        createAgent(name, "mkf.jade.guessinggame.Player");
      }
      createAgent("Host", "mkf.jade.guessinggame.Host");
	}

  private void createAgent(String name, String className) {
      	AID agentID = new AID( name, AID.ISLOCALNAME );
      	AgentContainer controller = getContainerController();
      	try {
      		AgentController agent = controller.createNewAgent( name, className, null );
      		agent.start();
      		System.out.println("+++ Created: " + agentID);
      	}
      	catch (Exception e){ e.printStackTrace(); }
  }

}
