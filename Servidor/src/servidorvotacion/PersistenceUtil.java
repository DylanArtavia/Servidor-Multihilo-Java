/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorvotacion;

/**
 * Utilidad encargada de guardar y cargar objetos usando serialización.
 * La idea es ofrecer un manejo seguro de archivos para evitar que se dañen
 * si ocurre un fallo durante la escritura.
 *
 * El guardado se hace de forma atómica: primero se escribe en un archivo
 * temporal y solo cuando todo se escribió sin errores se reemplaza el archivo real.
 * Esto reduce muchísimo el riesgo de corrupción.
 * 
 * Autor: Dylan
 */
import java.io.*;

public class PersistenceUtil {

    /**
     * Guarda un objeto en un archivo utilizando un proceso atómico.
     * Primero crea un archivo temporal, escribe el objeto allí
     * y luego sustituye el archivo original. Si el renombrado falla,
     * se usa una copia manual como respaldo.
     *
     * obj: objeto que se desea guardar.
     * target: archivo final donde debe quedar almacenado.
     */
    public static void saveObjectAtomic(Object obj, File target) throws IOException {
        File tmp = new File(target.getAbsolutePath() + ".tmp");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmp))) {
            oos.writeObject(obj);
            oos.flush();
        }

        // Se intenta reemplazar el archivo original directamente.
        if (target.exists()) target.delete();

        if (!tmp.renameTo(target)) {
            // Si no se pudo renombrar (Windows a veces molesta), se copia el archivo manualmente.
            try (FileInputStream fis = new FileInputStream(tmp);
                 FileOutputStream fos = new FileOutputStream(target)) {

                byte[] buf = new byte[8192];
                int r;

                while ((r = fis.read(buf)) != -1) {
                    fos.write(buf, 0, r);
                }
            }

            tmp.delete();
        }
    }

    /**
     * Carga un objeto desde un archivo previamente guardado.
     * El archivo debe contener un objeto serializado que coincida
     * con el tipo esperado.
     *
     * f: archivo desde donde se leerá.
     * clazz: tipo de objeto que se espera obtener.
     *
     * Retorna el objeto cargado ya convertido al tipo apropiado.
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadObject(File f, Class<T> clazz) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object o = ois.readObject();
            return (T) o;
        }
    }
}
