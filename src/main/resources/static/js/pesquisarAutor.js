document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('pesquisaForm').addEventListener('submit', function(event) {
        event.preventDefault();
        
        const titulo = document.getElementById('titulo').value;
        
        fetch(`/autors/pesquisar-autor/resultados?titulo=${titulo}`, {
            method: 'GET' 
        })
        .then(response => response.json())
        .then(data => {
            if (data && Array.isArray(data)) {
                let resultHtml = '<h2>Resultados</h2>';
                
                data.forEach(autor => {
                    resultHtml += `
                        <div>
                            <p><strong>authorId:</strong> ${autor.authorId}</p>
                            <p><strong>Nome:</strong> ${autor.nome}</p>
                            <p><strong>DBLP:</strong> ${autor.dblp}</p>
                            <p><strong>ORCID:</strong> ${autor.orcid}</p>
                            <p><strong>H-Index:</strong> ${autor.hindex}</p>
                            <button data-autor='${encodeURIComponent(JSON.stringify(autor))}' onclick="salvarAutor(this)">Salvar</button>
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
            alert("Erro ao pesquisar o autor.");
        });
    });
});

function salvarAutor(button) {
    // Busca o objeto inteiro
    const autorData = decodeURIComponent(button.getAttribute('data-autor'));
    
    console.log("autor data:", autorData);
    
    if (!autorData) {
        alert("Dados do autor não encontrados.");
        return;
    }

    try {
        const autor = JSON.parse(autorData);

        // Envia pro servidor db
        fetch('/autores/salvar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(autor)
        })
        .then(response => response.json()) 
        .then(data => {
            if (data.status === 'ok') {
                alert("Autor salvo com sucesso!");
            } else {
                alert("Erro ao salvar o autor."); 
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            alert("Erro ao salvar o autor.");
        });
    } catch (error) {
        console.error("Erro ao parsear o JSON:", error);
        alert("Erro ao salvar o autor.");
    }
}
