document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('pesquisaForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const name = document.getElementById('name').value;

        fetch(`/autores/pesquisar-autor/resultados?name=${name}`, { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            if (data && Array.isArray(data) && data.length > 0) {
                let resultHtml = '<h2>Resultados</h2>';

                data.forEach(autor => {
                    console.log('Autor Object:', autor);
                    const encodedData = encodeURIComponent(JSON.stringify(autor));
                    console.log('Encoded Data:', encodedData);

                    resultHtml += `
                        <div>
                            <p><strong>authorId:</strong> ${autor.authorId}</p>
                            <p><strong>Nome:</strong> ${autor.name}</p>
                            <p><strong>DBLP:</strong> ${autor.dblp || 'N/A'}</p>
                            <p><strong>ORCID:</strong> ${autor.orcid || 'N/A'}</p>
                            <p><strong>H-Index:</strong> ${autor.hindex || 'N/A'}</p>
                            <button data-autor='${encodedData}' onclick="salvarAutor(this)">Salvar</button>
                        </div>
                    `;
                });

                document.getElementById('resultado').innerHTML = resultHtml;
            } else {
                document.getElementById('resultado').innerHTML = '<p>Nenhum resultado encontrado.</p>';
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            alert("Erro ao pesquisar o autor.");
        });
    });
});

function salvarAutor(button) {
    const autorData = decodeURIComponent(button.getAttribute('data-autor'));
    console.log("Autor Data:", autorData);

    if (!autorData || autorData.trim() === '') {
        alert("Dados do autor estão vazios ou inválidos.");
        return;
    }

    try {
        const autor = JSON.parse(autorData);
        console.log("Parseei o autor:", autor);

        fetch('/autores/salvar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
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
        alert("Erro ao salvar o autor. Dados inválidos.");
    }
}
