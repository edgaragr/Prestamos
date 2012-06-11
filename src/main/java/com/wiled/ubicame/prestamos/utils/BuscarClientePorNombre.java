import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class BuscarClientePorNombre implements AlgoritmoDeBusqueda{

	    private EntityManager em;
	    
	    private BuscarClientePorNombre (EntityManager entity) {
	        this.em = entity;
	    }
	    
	    public List<Cliente> buscarCliente(String nombre) {
	        Query q = em.createNamedQuery("Cliente.buscarNombre");
	        q.setParameter("nombre", nombre.toUpperCase());
	        
	        return q.getResultList();
	    }
	    
}