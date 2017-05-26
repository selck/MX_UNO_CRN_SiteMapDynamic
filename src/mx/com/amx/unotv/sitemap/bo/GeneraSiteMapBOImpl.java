package mx.com.amx.unotv.sitemap.bo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import mx.com.amx.unotv.sitemap.dto.NotaDTO;
import mx.com.amx.unotv.sitemap.dto.ParametrosDTO;
import mx.com.amx.unotv.sitemap.util.CargaProperties;
import mx.com.amx.unotv.sitemap.util.LlamadasWS;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;


public class GeneraSiteMapBOImpl implements IGeneraSiteMapBO {

	private final Logger log = Logger.getLogger(this.getClass().getName());
	
	ParametrosDTO parametrosDTO =new ParametrosDTO();
	LlamadasWS llamadasWS=null;
	
	public void generarXML() throws Exception {
		try {
			 //inicio del doc xml
				 DOMSource sourceRet  = new DOMSource();
				 CargaProperties cargaProperties=new CargaProperties();
				 parametrosDTO=cargaProperties.obtenerPropiedades("ambiente.resources.properties");
				 
				 String rutaArchivo=parametrosDTO.getRutaLocal()+parametrosDTO.getNombreSiteMap()+".xml";
				 
				 llamadasWS=new LlamadasWS(); 
				 List<NotaDTO> elementos=llamadasWS.getElementosInsertar(0);
				 int contador = 0;
				 log.info("elementos a Insertar: "+elementos.size());
				 DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		    	 DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		    	 org.w3c.dom.Document docXML = docBuilder.newDocument();
		    	 docXML.createTextNode("<?xml version='1.0' encoding='UTF-8'?>");
				 org.w3c.dom.Element rootElement = docXML.createElement("urlset");
		    	 //rootElement.setAttribute("xmlns:news","http://www.google.com/schemas/sitemap-news/0.9");
		    	 rootElement.setAttribute("xmlns","http://www.sitemaps.org/schemas/sitemap/0.9");
		    	 //rootElement.setAttribute("xmlns:news","http://www.sitemaps.org/schemas/sitemap/0.9");
		    	 
		    	 docXML.appendChild(rootElement);
		    	 
		    	 for(NotaDTO tmp:elementos){
		    		 
					org.w3c.dom.Element url = docXML.createElement("url");
					rootElement.appendChild(url);
					
					org.w3c.dom.Element loc = docXML.createElement("loc");					
					loc.appendChild(docXML.createCDATASection(tmp.getFcLinkDetalle()));

					org.w3c.dom.Element lastmod = docXML.createElement("lastmod");					
					lastmod.appendChild(docXML.createCDATASection(tmp.getFcFechaModificacion()));
					
					org.w3c.dom.Element changefreq = docXML.createElement("changefreq");					
					changefreq.appendChild(docXML.createCDATASection("never"));
					
					org.w3c.dom.Element priority = docXML.createElement("priority");					
					priority.appendChild(docXML.createCDATASection("0.5"));
					
					url.appendChild(loc);
					url.appendChild(lastmod);
					url.appendChild(changefreq);
					url.appendChild(priority);

			        //log.info("Actualizara estado para id "+tmp.getFcIdContenido());
			        llamadasWS.actualizarEstatusElemento(tmp.getFcIdContenido());
			        contador ++;
		    	 }
		    	log.info("Total de elementos actualizados: "+contador); 
	    	 	TransformerFactory transformerFactory = TransformerFactory.newInstance();
        		Transformer transformer = transformerFactory.newTransformer();
        		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        		DOMSource source = new DOMSource(docXML);

        		sourceRet = source;

        		//File f = new File ("c:/siteMap/" + stNombreArchivo );
        		File f = new File (rutaArchivo );
        		StreamResult result = new StreamResult( f );
        		transformer.transform(sourceRet, result);
		        log.info("Archivo "+rutaArchivo+" generado Satisfactoriamente ");
		        if(parametrosDTO.getAmbiente().equalsIgnoreCase("desarrollo"))
		        	transfiereWebServer(parametrosDTO);
				 
		      } catch (Exception e) {
		    	  log.error("Error en generarXML: ",e);
		      }
	}
	public void agregarElementoXML() throws Exception {
		try {
			 DOMSource sourceRet  = new DOMSource();
			 CargaProperties cargaProperties=new CargaProperties();
			 parametrosDTO=cargaProperties.obtenerPropiedades("ambiente.resources.properties");
			 
			 String nombreArchivo=parametrosDTO.getRutaLocal()+parametrosDTO.getNombreSiteMap()+".xml";
			 llamadasWS=new LlamadasWS();
			 
			 List<NotaDTO> elementos=llamadasWS.getElementosInsertar(0);
			 log.info("elementos.size: "+elementos.size());
			 int contador = 0;
			 File inputFile = new File(nombreArchivo);
		     DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		     DocumentBuilder builder = factory.newDocumentBuilder();
		     // creating input stream
		     org.w3c.dom.Document docXML = builder.parse(inputFile);
		     
		     //org.w3c.dom.Element rootElement = docXML.getDocumentElement();
		     Node n = docXML.getElementsByTagName("url").item(0);//kaka
		     for(NotaDTO tmp:elementos){
		    		org.w3c.dom.Element url = docXML. createElement("url");
					
					org.w3c.dom.Element loc = docXML.createElement("loc");					
					loc.appendChild(docXML.createCDATASection(tmp.getFcLinkDetalle()));

					org.w3c.dom.Element lastmod = docXML.createElement("lastmod");					
					lastmod.appendChild(docXML.createCDATASection(tmp.getFcFechaModificacion()));
					
					org.w3c.dom.Element changefreq = docXML.createElement("changefreq");					
					changefreq.appendChild(docXML.createCDATASection("never"));
					
					org.w3c.dom.Element priority = docXML.createElement("priority");					
					priority.appendChild(docXML.createCDATASection("0.5"));
					
					url.appendChild(loc);
					url.appendChild(lastmod);
					url.appendChild(changefreq);
					url.appendChild(priority);
					
					//rootElement.appendChild(url);
					n.insertBefore(url, n.getFirstChild());
					//log.info("Actualizara estado para id "+tmp.getFcIdContenido());
			        llamadasWS.actualizarEstatusElemento(tmp.getFcIdContenido());
			        contador ++;
		     }
		     
		     log.info("Total de elementos actualizados: "+contador);
		     TransformerFactory transformerFactory = TransformerFactory.newInstance();
     		 Transformer transformer = transformerFactory.newTransformer();
     		 transformer.setOutputProperty(OutputKeys.INDENT, "yes");
     		 DOMSource source = new DOMSource(docXML);

     		 sourceRet = source;

     		 File f = new File (nombreArchivo );
     		 StreamResult result = new StreamResult( f );
     		 transformer.transform(sourceRet, result);
		     
			 log.info("Archivo "+nombreArchivo+" actualizado!");
			 if(parametrosDTO.getAmbiente().equalsIgnoreCase("desarrollo"))
				 transfiereWebServer(parametrosDTO);
		} catch (Exception e) {
			log.error("Error en agregarElementoXML: ",e);
		}
	}
    public Integer verificarTamanio(String rutaArchivo) throws Exception {
		Integer tamanio=0;
		try{
			File fichero = new File(rutaArchivo);
			if(fichero.exists())
				tamanio=Integer.parseInt(String.valueOf(fichero.length()));
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
		return tamanio;
	}

	
	public void inicializaProceso() throws Exception {
		log.info("En inicializaProceso ");
		try{
			CargaProperties cargaProperties=new CargaProperties();
			parametrosDTO=cargaProperties.obtenerPropiedades("ambiente.resources.properties");
			Integer tabulador=Integer.parseInt(parametrosDTO.getTamanioArchivo());
			String urlArchivo=parametrosDTO.getRutaLocal()+ parametrosDTO.getNombreSiteMap()+".xml";
			boolean continua=existeArchivo(urlArchivo);
			log.info("existeArchivo: "+continua);
			if(continua){
				Integer actual=verificarTamanio(urlArchivo);
				log.info("Archivo existe, tamanio actual: "+actual);
				if(actual < tabulador ){
					log.info("Seguimos agregando elementos al xml ");
					agregarElementoXML();
				}else{
					log.info("El archivo a sobrepasado el limite que es 10mb se creara otro");
					generaNuevoArchivo();
					generarXML();
				}
			}else{
				log.info("No existe Archivo, lo creamos...");
				generarXML();
			}
				
		}catch(Exception e)
		{
			throw new Exception(e.getMessage());
		}
		
	}
	
	public void generaNuevoArchivo() throws Exception{
		try{
			 CargaProperties cargaProperties=new CargaProperties();
			 parametrosDTO=cargaProperties.obtenerPropiedades("ambiente.resources.properties");
			 String nombreArchivo=parametrosDTO.getRutaLocal()+parametrosDTO.getNombreSiteMap()+".xml";
			 llamadasWS=new LlamadasWS();
			 Integer secuenciaSiguiente=llamadasWS.getSecuencia();
			 String nombreCambio= parametrosDTO.getRutaLocal()+parametrosDTO.getNombreSiteMap()+"_"+secuenciaSiguiente+".xml";
			 File fichero = new File(nombreArchivo);
			 boolean correcto = fichero.renameTo(new File(nombreCambio));
			 if(correcto){
				 log.info("Se renombro el archivo correctamente");
				 //log.info("Segun se envia correo");
				 //EnviaCorreo envia=new EnviaCorreo();
				 //envia.sendMail(nombreCambio);
			 } else
				 log.info("Error al renombrar el archivo");
		}catch(Exception e){
			log.error("Error generaNuevoArchivo: "+e.getMessage());
			throw new Exception(e.getMessage());
		}
		
	}
	
	public static void main(String args[]){
		try {
			String rutaArchivo="C:/pruebas/noticias/sitemaps/sitemap-historico.xml";
			boolean ban=existeArchivo2(rutaArchivo);
			if(ban){
				System.out.println("Ya existe el sitemap, procedemos a agregar nuevos items");
				GeneraSiteMapBOImpl boImpl=new GeneraSiteMapBOImpl();
				boImpl.agregarElementoXML(rutaArchivo);
			}else{
				System.out.println("No existe, procedemos a generar un nuevo sitemap");
				GeneraSiteMapBOImpl boImpl=new GeneraSiteMapBOImpl();
				boImpl.generarXML2(rutaArchivo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static List<NotaDTO> getFakeList(int inicio, int tope){
		List<NotaDTO> lista=new ArrayList<NotaDTO>();
		try {
			for (int i = inicio; i < tope; i++) {
				NotaDTO dto=new NotaDTO();
				dto.setFcIdContenido("id_contenido_"+i);
				dto.setFcFechaModificacion("1_diciembre_2015_"+i);
				dto.setFcLinkDetalle("link_de_la_nota_"+i);
				dto.setFcNombre("Nombre_de_la_nota_"+i);
				dto.setFcTitulo("Titulo_de_la_nota_"+i);
				lista.add(dto);
			}
		} catch (Exception e) {
			System.out.println("Error getFakeList: "+e.getMessage());
		}
		return lista;
	}
	public void agregarElementoXML(String rutaArchivo){
		try {
			 DOMSource sourceRet  = new DOMSource();
			 List<NotaDTO> elementos=getFakeList(1,10);
			 System.out.println("Agregar elementos a "+rutaArchivo);
			 System.out.println("Num de elementos a actualizar");
			 int contador = 0;
			 File inputFile = new File(rutaArchivo);
		     DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		     DocumentBuilder builder = factory.newDocumentBuilder();
		     // creating input stream
		     //Document docXML = (Document) builder.parse(inputFile );
		     org.w3c.dom.Document docXML = builder.parse(inputFile);
		     
		     //org.w3c.dom.Element rootElement = docXML.createElement("urlset");
		     org.w3c.dom.Element rootElement = docXML.getDocumentElement();
		     Node n = docXML.getElementsByTagName("url").item(0);//kaka
		     for(NotaDTO tmp:elementos){
		    		org.w3c.dom.Element url = docXML. createElement("url");
					
					org.w3c.dom.Element loc = docXML.createElement("loc");					
					loc.appendChild(docXML.createCDATASection(tmp.getFcLinkDetalle()));

					org.w3c.dom.Element lastmod = docXML.createElement("lastmod");					
					lastmod.appendChild(docXML.createCDATASection(tmp.getFcFechaModificacion()));
					
					org.w3c.dom.Element changefreq = docXML.createElement("changefreq");					
					changefreq.appendChild(docXML.createCDATASection("never"));
					
					org.w3c.dom.Element priority = docXML.createElement("priority");					
					priority.appendChild(docXML.createCDATASection("0.5"));
					
					url.appendChild(loc);
					url.appendChild(lastmod);
					url.appendChild(changefreq);
					url.appendChild(priority);
					
					//rootElement.appendChild(url);
					n.insertBefore(url, n.getFirstChild());
					//log.info("Actualizara estado para id "+tmp.getFcIdContenido());
			        //llamadasWS.actualizarEstatusElemento(tmp.getFcIdContenido());
					contador ++;
		     }
		     
		     System.out.println("Numero de items actualizados: "+contador);
		     TransformerFactory transformerFactory = TransformerFactory.newInstance();
     		 Transformer transformer = transformerFactory.newTransformer();
     		 transformer.setOutputProperty(OutputKeys.INDENT, "yes");
     		 DOMSource source = new DOMSource(docXML);

     		 sourceRet = source;

     		 File f = new File (rutaArchivo );
     		 StreamResult result = new StreamResult( f );
     		 transformer.transform(sourceRet, result);
		     
			 log.info("Archivo "+rutaArchivo+" actualizado!");
			 
		} catch (Exception e) {
			System.out.println("Error al actualizar el sitemap: "+e.getMessage());
			e.printStackTrace();
		}
	}
	public void generarXML2(String rutaArchivo){
		System.out.println("generarXML2");
		String basews="http://dev-unotv.tmx-internacional.net/MX_UNO_WSD_SiteMap/rest/siteMapController/";
		try {
			 //inicio del doc xml
				 DOMSource sourceRet  = new DOMSource();
				  
				 List<NotaDTO> elementos=getFakeList(0,10);
				 int contador = 0;
				 System.out.println("elementos a Insertar: "+elementos.size());
				 DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		    	 DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		    	 org.w3c.dom.Document docXML = docBuilder.newDocument();
		    	 docXML.createTextNode("<?xml version='1.0' encoding='UTF-8'?>");
				 org.w3c.dom.Element rootElement = docXML.createElement("urlset");
		    	 //rootElement.setAttribute("xmlns:news","http://www.google.com/schemas/sitemap-news/0.9");
		    	 rootElement.setAttribute("xmlns","http://www.sitemaps.org/schemas/sitemap/0.9");
		    	 //rootElement.setAttribute("xmlns:news","http://www.sitemaps.org/schemas/sitemap/0.9");
		    	 
		    	 docXML.appendChild(rootElement);
		    	 
		    	 for(NotaDTO tmp:elementos){
		    		 
					org.w3c.dom.Element url = docXML.createElement("url");
					rootElement.appendChild(url);
					
					org.w3c.dom.Element loc = docXML.createElement("loc");					
					loc.appendChild(docXML.createCDATASection(tmp.getFcLinkDetalle()));

					org.w3c.dom.Element lastmod = docXML.createElement("lastmod");					
					lastmod.appendChild(docXML.createCDATASection(tmp.getFcFechaModificacion()));
					
					org.w3c.dom.Element changefreq = docXML.createElement("changefreq");					
					changefreq.appendChild(docXML.createCDATASection("never"));
					
					org.w3c.dom.Element priority = docXML.createElement("priority");					
					priority.appendChild(docXML.createCDATASection("0.5"));
					
					url.appendChild(loc);
					url.appendChild(lastmod);
					url.appendChild(changefreq);
					url.appendChild(priority);

			        //log.info("Actualizara estado para id "+tmp.getFcIdContenido());
			        //llamadasWS.actualizarEstatusElemento(tmp.getFcIdContenido());
			        contador ++;
		    	 }
		    	 System.out.println("Total de elementos actualizados: "+contador); 
	    	 	TransformerFactory transformerFactory = TransformerFactory.newInstance();
        		Transformer transformer = transformerFactory.newTransformer();
        		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        		DOMSource source = new DOMSource(docXML);

        		sourceRet = source;

        		//File f = new File ("c:/siteMap/" + stNombreArchivo );
        		File f = new File (rutaArchivo );
        		StreamResult result = new StreamResult( f );
        		transformer.transform(sourceRet, result);
        		System.out.println("Archivo "+rutaArchivo+" generado Satisfactoriamente ");
		      } catch (Exception io) {
		    	  System.out.println("Error veda: "+io.getMessage());
		    	  io.printStackTrace();
		    	  
		      }
	}
	
	public boolean existeArchivo(String rutaArchivo ) throws Exception{
		boolean flag=false;
		try{				 
			 File fichero = new File(rutaArchivo);
			 if(fichero.exists()){
				 flag=true;
			 }else
				 flag=false;
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
		return flag;
	}
	public static boolean existeArchivo2(String rutaArchivo ) throws Exception{
		boolean flag=false;
		try{				 
			 File fichero = new File(rutaArchivo);
			 if(fichero.exists()){
				 flag=true;
			 }else
				 flag=false;
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
		return flag;
	}

	public void transfiereWebServer(ParametrosDTO parametrosDTO) throws Exception {
		try {	
				//Properties propiedades = new Properties();
			 	//propiedades.load(this.getClass().getResourceAsStream( "/ApplicationResources.properties" ));
			 	String rutaShell=parametrosDTO.getRutaShell();
			 	String pathLocal=parametrosDTO.getRutaLocal();
			 	String pathRemote=parametrosDTO.getRutaWebServer();
			 	String comando = rutaShell + " " + pathLocal + " " + pathRemote;		
				log.info("Comando: "+comando);
				Runtime r = Runtime.getRuntime();
				r.exec(comando);
			} catch(Exception e) {
				throw new Exception(e.getMessage());
			}		
		
	}
	
	public void inciaSecuencia() throws Exception{
		try {
			llamadasWS=new LlamadasWS(); 
			llamadasWS.getSecuencia();
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}	
	}
	
	public void obtieneSecuenciaActual() throws Exception{
		try {
			llamadasWS=new LlamadasWS(); 
			llamadasWS.getSecuenciaActual();
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}	
	}
}
