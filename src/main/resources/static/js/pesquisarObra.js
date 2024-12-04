document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('pesquisaForm').addEventListener('submit', function(event) {
        event.preventDefault();
        
        const titulo = document.getElementById('titulo').value;
        
        fetch(`/obras/pesquisar-obra/resultados?titulo=${titulo}`, {
            method: 'GET' 
        })
        .then(response => response.json())
        .then(data => {
            if (data && Array.isArray(data)) {
                let resultHtml = '<h2>Resultados</h2>';
                
                data.forEach(obra => {
                    resultHtml += `
                        <div>
                            <p><strong>Paper ID:</strong> ${obra.paperId}</p>
                            <p><strong>Título:</strong> ${obra.title}</p>
                            <p><strong>DOI:</strong> ${obra.doi}</p>
                            <p><strong>Ano:</strong> ${obra.year}</p>
                            <p><strong>TLDR:</strong> ${obra.tldr}</p>
                            <button data-obra='${encodeURIComponent(JSON.stringify(obra))}' onclick="salvarObra(this)">Salvar</button>
                        </div>
                    `;
                });
                
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
    // Busca o objeto inteiro
    const obraData = decodeURIComponent(button.getAttribute('data-obra'));
    
    console.log("Obra data:", obraData);
    
    if (!obraData) {
        alert("Dados da obra não encontrados.");
        return;
    }

    try {
        const obra = JSON.parse(obraData);

        // Envia pro servidor db
        fetch('/obras/salvar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(obra)
        })
        .then(response => response.json()) 
        .then(data => {
            if (data.status === 'ok') {
                alert("Obra salva com sucesso!");
            } else {
                alert("Erro ao salvar a obra."); 
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
