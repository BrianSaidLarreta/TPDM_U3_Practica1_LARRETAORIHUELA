package com.example.tpdm_u3_practica1_larretaorihuela;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MHospitales extends AppCompatActivity {
    EditText id, nombre, domicilio, capacidad, anio;
    Button insertar, eliminar, actualizar, buscar;
    private DatabaseReference mDatabase;
    List<Hospitales> datos;
    ListView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mhospitales);

        id = findViewById(R.id.idH);
        nombre = findViewById(R.id.nom);
        domicilio = findViewById(R.id.dom);
        capacidad = findViewById(R.id.cap);
        anio = findViewById(R.id.anio);
        insertar = findViewById(R.id.btnInsertar);
        eliminar = findViewById(R.id.btnEliminar);
        actualizar = findViewById(R.id.btnModificar);
        buscar = findViewById(R.id.btnMostrar);
        lista = findViewById(R.id.listaHospitales);

        eliminar.setEnabled(false);
        actualizar.setEnabled(false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarHospital();
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarHospital();
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarHospital();
            }
        });
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarHospital();
            }
        });

        //llena la lista con datos
        mDatabase.child("hospitales").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datos = new ArrayList<>();

                if(dataSnapshot.getChildrenCount()<=0){
                    Toast.makeText(MHospitales.this, "No hay hospitales registrados", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(final DataSnapshot snap : dataSnapshot.getChildren()){
                    mDatabase.child("hospitales").child(snap.getKey()).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Hospitales u = dataSnapshot.getValue(Hospitales.class);

                                    if(u!=null){
                                        datos.add(u);
                                    }
                                    cargarSelect();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private  void insertarHospital(){
        final Hospitales h = new Hospitales(id.getText().toString(),nombre.getText().toString(), domicilio.getText().toString(),capacidad.getText().toString(), anio.getText().toString());
        if(h.id.isEmpty() || h.domicilio.isEmpty() || h.anio.isEmpty() || h.nombre.isEmpty() || h.capacidad.isEmpty()){
            Toast.makeText(MHospitales.this, "REVISE SU INFORMACIÓN. CAMPOS VACÍOS", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabase.child("hospitales").child(h.id).setValue(h)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MHospitales.this, "OPERACIÓN EXITOSA", Toast.LENGTH_SHORT).show();
                        id.setText("");nombre.setText("");domicilio.setText("");anio.setText("");capacidad.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MHospitales.this, "A OCURRIDO UN ERROR!!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private  void eliminarHospital(){
        final EditText idhospital = new EditText(this);
        idhospital.setHint("ID DEL HOSPITAL");
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("VALOR A BUSCAR:").setView(idhospital).setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminar(idhospital.getText().toString());
            }
        }).setNegativeButton("Cancelar", null).show();
    }

    private void eliminar(String idH){
        mDatabase.child("hospitales").child(idH).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MHospitales.this, "OPERACIÓN EXITOSA", Toast.LENGTH_SHORT).show();
                        id.setText("");nombre.setText("");anio.setText("");domicilio.setText("");capacidad.setText("");
                        eliminar.setEnabled(false);
                        actualizar.setEnabled(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MHospitales.this, "ERROR AL ELIMINAR", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarHospital(){
        final EditText idhospital = new EditText(this);
        idhospital.setHint("ID A BUSCAR");
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("VALOR A BUSCAR:").setView(idhospital).setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mostrar(idhospital.getText().toString());
            }
        }).setNegativeButton("Cancelar", null).show();
    }


    private void mostrar(String i){
        FirebaseDatabase.getInstance().getReference().child("hospitales").child(i)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Hospitales h = dataSnapshot.getValue(Hospitales.class);

                        if(h!=null) {
                            id.setText(h.id);
                            nombre.setText(h.nombre);
                            capacidad.setText(h.capacidad);
                            domicilio.setText(h.domicilio);
                            anio.setText(h.anio);
                            eliminar.setEnabled(true);
                            actualizar.setEnabled(true);
                        } else {
                            mensaje("Error","No se encontró hospital a mostrar");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void mensaje(String t, String m){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);

        alerta.setTitle(t).setMessage(m).setPositiveButton("OK",null).show();
    }

    private void cargarSelect(){
        if (datos.size()==0) return;
        String nombres[] = new String[datos.size()];

        for(int i = 0; i<nombres.length; i++){
            Hospitales u = datos.get(i);
            nombres[i] = u.nombre;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
        lista.setAdapter(adapter);
    }
    private void actualizarHospital(){
        Hospitales hos = new Hospitales(id.getText().toString(),nombre.getText().toString(), capacidad.getText().toString(),domicilio.getText().toString(), anio.getText().toString());

        mDatabase.child("hospitales").child(id.getText().toString()).setValue(hos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MHospitales.this, "OPERACIÓN EXITOSA", Toast.LENGTH_SHORT).show();
                        id.setText("");nombre.setText("");capacidad.setText("");domicilio.setText("");anio.setText("");
                        eliminar.setEnabled(false);
                        actualizar.setEnabled(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MHospitales.this, "ERROR AL ELIMINAR", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
