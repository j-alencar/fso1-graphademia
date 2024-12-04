document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('pesquisaForm').addEventListener('submit', function(event) {
        event.preventDefault();
        
        const titulo = document.getElementById('titulo').value;
        
        fetch(`/obras/pesquisa/resultados?titulo=${titulo}`, {
            method: 'GET' 
        })
        .then(response => response.json())
        .then(data => {
            if (data) {
                let resultHtml = `
                    <h2>Resultado da Pesquisa</h2>
                    <p><strong>Paper ID:</strong> ${data.paperId}</p>
                    <p><strong>Título:</strong> ${data.title}</p>
                    <p><strong>DOI:</strong> ${data.doi}</p>
                    <p><strong>Data de Publicação:</strong> ${data.publicationDate}</p>
                    <p><strong>TLDR:</strong> ${data.tldr}</p>
                    <button data-obra='${encodeURIComponent(JSON.stringify(data))}' onclick="salvarObra(this)">Salvar</button>
                `;
                document.getElementById('resultado').innerHTML = resultHtml;
            } else {
                alert("Nenhum resultado encontrado.");
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            alert("Erro ao pesquisar a obra.");
        });
    });
});

function salvarObra(button) {
    // Retrieve the entire Obra object from the button's data attribute
    const obraData = decodeURIComponent(button.getAttribute('data-obra'));
    
    console.log("Obra data:", obraData); // Log the data to see what you're working with
    
    if (!obraData) {
        alert("Dados da obra não encontrados.");
        return; // Prevent further execution if the data is empty
    }

    try {
        const obra = JSON.parse(obraData); // Parse the JSON data

        // Send the entire obra data to the server
        fetch('/obras/salvar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(obra) // Send the entire obra object as JSON
        })
        .then(response => response.json()) // Parse the response as JSON
        .then(data => {
            if (data.status === 'ok') {
                alert("Obra salva com sucesso!"); // Success message
            } else {
                alert("Erro ao salvar a obra."); // Error message
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            alert("Erro ao salvar a obra.");
        });
    } catch (error) {
        console.error("Erro ao parsear o JSON:", error);
        alert("Erro ao salvar a obra.");
    }
}
