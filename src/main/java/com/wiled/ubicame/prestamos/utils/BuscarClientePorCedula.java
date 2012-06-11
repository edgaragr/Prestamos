import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class BuscarClientePorCedula implements AlgoritmoDeBusqueda{

    private EntityManager em;
    
    private BuscarClientePorCedula (EntityManager entity) {
        this.em = entity;
    }
    
    public List<Cliente> buscarCliente(String cedula) {
        Query q = em.createNamedQuery("Cliente.buscarCedula");
        q.setParameter("nombre", cedula);
        
        return q.getResultList();
    }
    
}