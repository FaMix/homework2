const express = require('express');
const fs = require('fs');
const { exec } = require('child_process');  // Execute external programs
const app = express();
const port = 3000;
const cors = require('cors');  // Add this line

app.use(cors());  // Add this line
app.use(express.json());

// Route to save strings in 'input.txt' and call search_engine to process them
app.post('/saveStrings', (req, res) => {
    let { string1, string2, string3 } = req.body;

    // If any string is empty, assign "undefined"
    if (!string1) string1 = 'undefined';
    if (!string2) string2 = 'undefined';
    if (!string3) string3 = 'undefined';

    // Save the strings in 'input.txt'
    const content = `Author: ${string1}\nTitle: ${string2}\nTopic: ${string3}\n`;
    fs.writeFileSync('input.txt', content, 'utf8');

    // Call the search_engine program to process the strings
    exec('java -cp "../target/classes;../target/dependency/*" homework2.SearchEngine', (error, stdout, stderr) => {
        if (error) {
            console.error(`Error executing SearchEngine: ${error.message}`);
            return res.status(500).send('Error executing SearchEngine');
        }
        if (stderr) {
            console.error(`stderr: ${stderr}`);
            return res.status(500).send('Error executing SearchEngine');
        }

        // If the program runs successfully, read the result from 'output.txt'
        fs.readFile('output.txt', 'utf8', (err, data) => {
            if (err) {
                return res.status(500).send('Error reading output.txt');
            }
            res.status(200).send(data);  // Return the reversed result to the client
        });
    });
});

// Route to read the reversed strings from 'output.txt'
app.get('/readStrings', (req, res) => {
    fs.readFile('../resources/output.txt', 'utf8', (err, data) => {
        if (err) {
            res.status(500).send('Error reading the file');
            return;
        }
        res.status(200).send(data);
    });
});

// Start the server
app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});
