import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ReplicaManager extends UnicastRemoteObject
    implements PlacesListManagerInterface, ReplicasManagementInterface {
  // Attributes
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static ArrayList<String> replicas = new ArrayList<String>();

  // Constructor
  public ReplicaManager() throws RemoteException {
    super(0);

    // Pings all replicas
    Thread t = new Thread() {
      public void run() {

        MonitoringInterface m = null;

        while (true) {

          for (String url : replicas) {
            try {
              // Testa se a replica está up
              m = (MonitoringInterface) Naming.lookup(url);
              m.ping();
            } catch (MalformedURLException | RemoteException | NotBoundException e) {
              // Relaunch replica
              
              String port = url.split(":")[2].split("/")[0];
              try {
                removeReplica(url);
              } catch (RemoteException e1) {
                e1.printStackTrace();
              }
              RMIServer.main(new String[] {port});
            }
          }

          // Sleep da Thread a cada 1s
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    };
    t.start();
  }

  @Override
  public synchronized void addPlace(Place p) throws RemoteException {
    for (String url : replicas) {
      invokeObjectRegistry(p);
      addObjectReplica(p, url);
    }
  }

  @Override
  public synchronized String getPlaceListAddress(String objectID) throws RemoteException {
    if (!replicas.isEmpty()) {
      return replicas.get((int) (Math.random() * replicas.size()));
    }
    return null;
  }

  private void invokeObjectRegistry(Place p) {
    ObjectRegistryInterface o = null;
    try {
      o = (ObjectRegistryInterface) Naming.lookup("rmi://localhost:2023/registry");
      o.addRManager(p.getPostalCode(), "rmi://localhost:2024/replicamanager");
    } catch (MalformedURLException | RemoteException | NotBoundException e) {
      e.printStackTrace();
    }
  }

  private void addObjectReplica(Place place, String url) {
    PlacesListInterface p = null;
    try {
      p = (PlacesListInterface) Naming.lookup(url);
      p.addPlace(place);
    } catch (MalformedURLException | RemoteException | NotBoundException e) {
      e.printStackTrace();
    }

  }

  @Override
  public synchronized String addReplica(String replicaAddress) throws RemoteException {
    String replica = getPlaceListAddress("");
    replicas.add(replicaAddress);
    return replica;
  }

  @Override
  public synchronized void removeReplica(String replicaAddress) throws RemoteException {
    replicas.remove(replicaAddress);
  }
}
