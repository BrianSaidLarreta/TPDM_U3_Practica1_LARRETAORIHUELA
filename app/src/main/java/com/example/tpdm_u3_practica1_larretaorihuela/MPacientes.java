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

public class MPacientes extends AppCompatActivity {
    EditText id,nombre, edad, padecimiento, telefono;
    Button insertar, eliminar, modificar, mostrar;
    private DatabaseReference mDatabase;
    List<Pacientes> datos;
    ListView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpacientes);

        id = findViewById(R.id.idP);
        nombre = findViewById(R.id.nomP);
        edad = findViewById(R.id.edad);
        padecimiento = findViewById(R.id.pad);
        telefono = findViewById(R.id.tel);
        insertar = findViewById(R.id.btnInsertarP);
        eliminar = findViewById(R.id.btnEliminarP);
        modificar = findViewById(R.id.btnModificarP);
        mostrar = findViewById(R.id.btnMostrarP);
        lista = findViewById(R.id.listaPacientes);

        eliminar.setEnabled(false);
        modificar.setEnabled(false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarPaciente();
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarPaciente();
            }
        });
        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarPaciente();
            }
        });
        mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPaciente();
            }
        });

        //llena la lista con datos
        mDatabase.child("pacientes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datos = new ArrayList<>();

                if(dataSnapshot.getChildrenCount()<=0){
                    Toast.makeText(MPacientes.this, "No hay pacientes registrados", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(final DataSnapshot snap : dataSnapshot.getChildren()){
                    mDatabase.child("pacientes").child(snap.getKey()).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Pacientes u = dataSnapshot.getValue(Pacientes.class);

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

    private  void insertarPaciente(){
        final Pacientes p = new Pacientes(id.getText().toString(),nombre.getText().toString(), edad.getText().toString(),padecimiento.getText().toString(), telefono.getText().toString());
        if(p.edad.isEmpty() || p.padecimiento.isEmpty() || p.nombre.isEmpty() || p.telefono.isEmpty()){
            Toast.makeText(MPacientes.this, "REVISE SU INFORMACIÓN. CAMPOS VACÍOS", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabase.child("pacientes").child(p.id).setValue(p)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MPacientes.this, "OPERACIÓN EXITOSA", Toast.LENGTH_SHORT).show();
                        id.setText("");nombre.setText("");edad.setText("");telefono.setText("");padecimiento.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MPacientes.this, "A OCURRIDO UN ERROR!!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private  void eliminarPaciente(){
        final EditText idpaciente = new EditText(this);
        idpaciente.setHint("TELEFONO DEL PACIENTE A ELIMINAR");
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("VALOR A BUSCAR:").setView(idpaciente).setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminar(idpaciente.getText().toString());
            }
        }).setNegativeButton("Cancelar", null).show();
    }

    private void eliminar(String idP){
        mDatabase.child("pacientes").child(idP).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MPacientes.this, "OPERACIÓN EXITOSA", Toast.LENGTH_SHORT).show();
                        id.setText("");nombre.setText("");edad.setText("");telefono.setText("");padecimiento.setText("");
                        eliminar.setEnabled(false);
                        modificar.setEnabled(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MPacientes.this, "ERROR AL ELIMINAR", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarPaciente(){
        final EditText idpaciente = new EditText(this);
        idpaciente.setHint("ID A BUSCAR");
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("VALOR A BUSCAR:").setView(idpaciente).setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mostrar(idpaciente.getText().toString());
            }
        }).setNegativeButton("Cancelar", null).show();
    }


    private void mostrar(String i){
        FirebaseDatabase.getInstance().getReference().child("pacientes").child(i)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Pacientes p = dataSnapshot.getValue(Pacientes.class);

                        if(p!=null) {
                            id.setText(p.id);
                            nombre.setText(p.nombre);
                            edad.setText(p.edad);
                            padecimiento.setText(p.padecimiento);
                            telefono.setText(p.telefono);
                            eliminar.setEnabled(true);
                            modificar.setEnabled(true);
                        } else {
                            mensaje("Error","No se encontró paciente a mostrar");
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
            Pacientes u = datos.get(i);
            nombres[i] = u.nombre;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
        lista.setAdapter(adapter);
    }
    private void actualizarPaciente(){
        Pacientes pac = new Pacientes(id.getText().toString(),nombre.getText().toString(), edad.getText().toString(),padecimiento.getText().toString(), telefono.getText().toString());

        mDatabase.child("pacientes").child(id.getText().toString()).setValue(pac)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MPacientes.this, "OPERACIÓN EXITOSA", Toast.LENGTH_SHORT).show();
                        id.setText("");nombre.setText("");edad.setText("");telefono.setText("");padecimiento.setText("");
                        eliminar.setEnabled(false);
                        modificar.setEnabled(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MPacientes.this, "ERROR AL ELIMINAR", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
