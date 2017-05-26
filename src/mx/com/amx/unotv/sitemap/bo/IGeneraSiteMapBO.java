package mx.com.amx.unotv.sitemap.bo;

public interface IGeneraSiteMapBO {

	public void generarXML() throws Exception;
	public void agregarElementoXML() throws Exception;
	public Integer verificarTamanio(String rutaArchivo) throws Exception;
	public void inicializaProceso() throws Exception;
	public boolean existeArchivo(String rutaArchivo) throws Exception;
	public void inciaSecuencia() throws Exception;
	public void obtieneSecuenciaActual() throws Exception;

}
