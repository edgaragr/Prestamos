import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class BuscarClientePorTelefono implements AlgoritmoDeBusqueda{

    private EntityManager em;
    
    private BuscarClientePorTelefono (EntityManager entity) {
        
        this.em = entity;
    }
    
    public List<Cliente> buscarCliente(String telefono) {
        Query q = em.createNamedQuery("Cliente.buscarTelefono");
        q.setParameter("nombre", telefono);
        
        return q.getResultList();
    }
    
}