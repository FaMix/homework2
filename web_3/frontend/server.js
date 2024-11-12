const express = require('express');
const fs = require('fs');
const { exec } = require('child_process');  // Ejecutar programas externos
const app = express();
const port = 3000;
const cors = require('cors');  // Add this line

app.use(cors());  // Add this line
app.use(express.json());

// Ruta para guardar las cadenas en 'input.txt' y llamar a search_engine para procesarlas
app.post('/guardarCadenas', (req, res) => {
    let { cadena1, cadena2, cadena3 } = req.body;
    
    
    // Si alguna cadena está vacía, asignar "undefined"
    if (!cadena1) cadena1 = 'undefined';
    if (!cadena2) cadena2 = 'undefined';
    if (!cadena3) cadena3 = 'undefined';

    // Guardar las cadenas en 'input.txt'
    const content = `Author: ${cadena1}\nTitle: ${cadena2}\nTopic: ${cadena3}\n`;
    fs.writeFileSync('input.txt', content, 'utf8');
    
    // Llamar al programa search_engine para procesar las cadenas
    exec('java search_engine', (error, stdout, stderr) => {
        if (error) {
            console.error(`Error al ejecutar search_engine: ${error.message}`);
            return res.status(500).send('Error al ejecutar search_engine');
        }
        if (stderr) {
            console.error(`stderr: ${stderr}`);
            return res.status(500).send('Error al ejecutar search_engine');
        }

        // Si el programa se ejecuta correctamente, leer el resultado de 'output.txt'
        fs.readFile('output.txt', 'utf8', (err, data) => {
            if (err) {
                return res.status(500).send('Error al leer output.txt');
            }
            res.status(200).send(data);  // Devolver el resultado invertido al cliente
        });
    });
});

// Ruta para leer las cadenas invertidas desde 'output.txt'
app.get('/leerCadenas', (req, res) => {
    fs.readFile('C:\Users\saioa\OneDrive\Escritorio\ROMA_TRE\Ingegneria_dei_dati\homework_2\web_3\src\main\resources\output.txt', 'utf8', (err, data) => {
        if (err) {
            res.status(500).send('Error al leer el archivo');
            return;
        }
        res.status(200).send(data);
    });
});

// Iniciar el servidor
app.listen(port, () => {
    console.log(`Servidor corriendo en http://localhost:${port}`);
});
