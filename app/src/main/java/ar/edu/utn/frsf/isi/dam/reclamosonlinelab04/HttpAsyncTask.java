package ar.edu.utn.frsf.isi.dam.reclamosonlinelab04;

import android.os.AsyncTask;
import android.text.BoringLayout;

import java.util.List;

import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.dao.ReclamoDao;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.dao.ReclamoDaoHTTP;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.Estado;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.Reclamo;

/**
 * Created by augusto on 07/11/2017.
 */

public class HttpAsyncTask extends AsyncTask {


    @Override
    protected Object doInBackground(Object[] objects) {

        ReclamoDao reclamoDao = new ReclamoDaoHTTP();
        Reclamo nuevoReclamo = (Reclamo) objects[0];
        Boolean esNuevo = (Boolean) objects[1];
        Boolean obtieneReclamos = (Boolean)objects[2];
        Boolean obtieneID = (Boolean)objects[3];
        int id = (int)objects[4];
        Boolean elimina = (Boolean)objects[5];

        List<Reclamo> reclamos;
        Estado estado;
        try {

            if (obtieneReclamos){
                reclamos = reclamoDao.reclamos();

                return reclamos;
            }
            if (obtieneID){
                estado = reclamoDao.getEstadoById(id);

                return estado;
            }
            if(elimina){
                reclamoDao.borrar(nuevoReclamo);
            }
            if(esNuevo){
                reclamoDao.crear(nuevoReclamo);
            }
            else if(!esNuevo){
                reclamoDao.actualizar(nuevoReclamo);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
