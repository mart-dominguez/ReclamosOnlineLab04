package ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.dao;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.Estado;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.Reclamo;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.TipoReclamo;

/**
 * Created by mdominguez on 26/10/17.
 */

public class ReclamoDaoHTTP implements ReclamoDao {

    private List<TipoReclamo> tiposReclamos = null;
    private List<Estado> tiposEstados = null;
    private List<Reclamo> listaReclamos = null;
    private String server;
    private MyGenericHTTPClient cliente;

    public ReclamoDaoHTTP(){
        server="http://10.0.2.2:3000";
        cliente = new MyGenericHTTPClient(server);
    }

    public ReclamoDaoHTTP(String server){
        this.server=server;
        cliente=new MyGenericHTTPClient(server);
    }


    @Override
    public List<Estado> estados() {
        tiposEstados = new ArrayList<>();
        if(tiposEstados!=null && tiposEstados.size()>0) return this.tiposEstados;
        else{
            String estadosJSON = cliente.getAll("estado");
            try {
                JSONArray arr = new JSONArray(estadosJSON);
                for(int i=0;i<arr.length();i++){
                    JSONObject unaFila = arr.getJSONObject(i);
                    tiposEstados.add(new Estado(unaFila.getInt("id"),unaFila.getString("tipo")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tiposEstados;
    }

    @Override
    public List<TipoReclamo> tiposReclamo() {
        tiposReclamos = new ArrayList<>();
        if(tiposReclamos!=null && tiposReclamos.size()>0) return this.tiposReclamos;
        else{
            String estadosJSON = cliente.getAll("tipo");
            try {
                JSONArray arr = new JSONArray(estadosJSON);
                for(int i=0;i<arr.length();i++){
                    JSONObject unaFila = arr.getJSONObject(i);
                    tiposReclamos.add(new TipoReclamo(unaFila.getInt("id"),unaFila.getString("tipo")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tiposReclamos;
    }

    @Override
    public List<Reclamo> reclamos() {
        listaReclamos = new ArrayList<>();
        String reclamosJSON = cliente.getAll("reclamo");
        try {
            JSONArray arr = new JSONArray(reclamosJSON);
            for(int i=0;i<arr.length();i++){
                JSONObject unaFila = arr.getJSONObject(i);
                Reclamo recTmp = new Reclamo();
                recTmp.setId(unaFila.getInt("id"));
                recTmp.setTitulo(unaFila.getString("titulo"));
                recTmp.setTipo(this.getTipoReclamoById(unaFila.getInt("tipoId")));
                recTmp.setEstado(this.getEstadoById(unaFila.getInt("estadoId")));
                listaReclamos.add(recTmp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaReclamos;
    }

    @Override
    public Estado getEstadoById(Integer id){
        Estado objResult =new Estado(99,"no encontrado");
        if(this.tiposEstados!=null){
            for(Estado e:tiposEstados){
                if(e.getId()==id) return e;
            }
        }else{
            String estadoJSON = cliente.getById("estado",id);
            try {
                JSONObject unaFila = new JSONObject(estadoJSON);
                objResult = new Estado(unaFila.getInt("id"),unaFila.getString("tipo"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return objResult;
    }

    @Override
    public TipoReclamo getTipoReclamoById(Integer id){
        TipoReclamo objResult =new TipoReclamo(99,"NO ENCONTRADO");
        if(this.tiposEstados!=null){
            for(TipoReclamo e:tiposReclamos){
                if(e.getId()==id) return e;
            }
        }else{
            String estadoJSON = cliente.getById("tipo",id);
            try {
                JSONObject unaFila = new JSONObject(estadoJSON);
                objResult = new TipoReclamo(unaFila.getInt("id"),unaFila.getString("tipo"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return objResult;
    }

    @Override
    public void crear(Reclamo r) {
        String detalle = r.getDetalle();
        Integer id = r.getId();
        String titulo = r.getTitulo();
        TipoReclamo tipo = r.getTipo();
        Date fecha = r.getFecha();
        Estado estado = r.getEstado();

        JSONObject nuevoReclamoJson = new JSONObject();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaStr = sdf.format(fecha);



        try {
            nuevoReclamoJson.put("id", id);
            nuevoReclamoJson.put("titulo", titulo);
            nuevoReclamoJson.put("detalle", detalle);
            nuevoReclamoJson.put("fecha", fechaStr);
            nuevoReclamoJson.put("tipoId",5);
            nuevoReclamoJson.put("estadoId", estado.getId());

            LatLng lugar = r.getLugar();
            if(lugar!=null) {
                Double latitud = lugar.latitude;
                Double longitud = lugar.longitude;
                String latStr = latitud.toString();
                String longStr = longitud.toString();
                nuevoReclamoJson.put("lugar", latStr + ";" + longStr);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        cliente.post("reclamo", nuevoReclamoJson.toString());

    }

    @Override
    public void actualizar(Reclamo r) {
        Integer id = r.getId();

        JSONObject reclamoJSON = new JSONObject();
        try {

            Date fecha = r.getFecha();

            reclamoJSON.put("id", id);
            reclamoJSON.put("titulo", r.getTitulo());
            reclamoJSON.put("detalle", r.getDetalle());
            if(fecha!=null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fechaStr = sdf.format(fecha);
                reclamoJSON.put("fecha", fechaStr);
            }
            reclamoJSON.put("tipoId",r.getTipo().getId());
            reclamoJSON.put("estadoId", r.getEstado().getId());

            LatLng lugar = r.getLugar();
            if(lugar!=null) {
                Double latitud = lugar.latitude;
                Double longitud = lugar.longitude;
                String latStr = latitud.toString();
                String longStr = longitud.toString();
                reclamoJSON.put("lugar", latStr + ";" + longStr);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        cliente.put("reclamo", id, reclamoJSON.toString());

    }

    @Override
    public void borrar(Reclamo r) {
        Integer id = r.getId();

        cliente.delete("reclamo", id);

    }
}
