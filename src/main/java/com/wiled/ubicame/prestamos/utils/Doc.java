import javax.print.*;

public class Doc implements Impresor{
	
	
	public void imprimirFactura(String factura){

	 PrintService service = PrintServiceLookup.lookupDefaultPrintService();
     if (service == null) {
         throw new PrintException("No se encontro impresora conectada");
     }

     //Le decimos el tipo de datos que vamos a enviar a la impresora
     //Tipo: bytes Subtipo: autodetectado
     DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
     DocPrintJob pj = service.createPrintJob();
     byte[] bytes;
     bytes = factura.getBytes();
     Doc doc = new SimpleDoc(bytes, flavor, null);

     pj.print(doc, null);

}
}
