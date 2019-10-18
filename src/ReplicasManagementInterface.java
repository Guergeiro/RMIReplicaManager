import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReplicasManagementInterface extends Remote {
  String addReplica(String replicaAddress) throws RemoteException;
  void removeReplica(String replicaAddress) throws RemoteException;
}
