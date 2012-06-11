import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class BuscarClientePorApellido implements AlgoritmoDeBusqueda{

    private EntityManager em;
    
    private BuscarClientePorApellido (EntityManager entity) {
        this.em = entity;
    }
    
    public List<Cliente> buscarCliente(String apellido) {
        Query q = em.createNamedQuery("Cliente.buscarApellido");
        q.setParameter("apellido", apellido.toUpperCase());
        
        return q.getResultList();
    }
    
}