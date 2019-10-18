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
  }

  // Constructor
  public ReplicaManager(String[] arrayURL) throws RemoteException {
    super(0);
    for (String url : arrayURL) {
      replicas.add(url);
    }
  }

  @Override
  public void addPlace(Place p) throws RemoteException {
    for (String url : replicas) {
      invokeObjectRegistry(p);
      addObjectReplica(p, url);
    }
  }

  @Override
  public String getPlaceListAddress(String objectID) throws RemoteException {
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
  public String addReplica(String replicaAddress) throws RemoteException {
    String replica = getPlaceListAddress("");
    replicas.add(replicaAddress);
    return replica;
  }

  @Override
  public void removeReplica(String replicaAddress) throws RemoteException {
    replicas.remove(replicaAddress);
  }
}
