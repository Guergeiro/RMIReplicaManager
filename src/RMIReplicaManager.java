import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIReplicaManager {
  public static void main(String[] args) {
    Registry r = null;
    try {
      r = LocateRegistry.createRegistry(2024);
    } catch (RemoteException a) {
      a.printStackTrace();
    }

    try {
      PlacesListManagerInterface placeList = new ReplicaManager();
      r.rebind("replicamanager", placeList);

      System.out.println("ReplicaManager server ready");
    } catch (Exception e) {
      System.out.println("ReplicaManager server main " + e.getMessage());
    }
  }
}
