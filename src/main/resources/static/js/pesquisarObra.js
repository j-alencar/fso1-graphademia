document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('pesquisaForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const titulo = document.getElementById('titulo').value;

        fetch(`/obras/pesquisar-obra/resultados?titulo=${titulo}`, {
            method: 'GET'
        })
            .then(response => response.json())
            .then(data => {
                if (data && Array.isArray(data) && data.length > 0) {
                    let resultHtml = `
                        <h2 class="text-center mb-4">Resultados</h2>
                        <div class="row g-4">
                    `;

                    data.forEach(obra => {
                        const encodedData = encodeURIComponent(JSON.stringify(obra));

                        resultHtml += `
                            <div class="col-md-4">
                                <div class="card shadow-sm">
                                    <div class="card-body">
                                        <h5 class="card-title">${obra.title}</h5>
                                        <p class="card-text"><strong>Paper ID:</strong> ${obra.paperId}</p>
                                        <p class="card-text"><strong>DOI:</strong> ${obra.doi || 'N/A'}</p>
                                        <p class="card-text"><strong>Ano:</strong> ${obra.year || 'N/A'}</p>
                                        <p class="card-text"><strong>TLDR:</strong> ${obra.tldr || 'N/A'}</p>
                                        <button class="btn btn-success w-100" data-obra="${encodedData}" onclick="salvarObra(this)">Salvar</button>
                                    </div>
                                </div>
                            </div>
                        `;
                    });

                    resultHtml += '</div>';
                    document.getElementById('resultado').innerHTML = resultHtml;
                } else {
                    showToast("Nenhum resultado encontrado.", "warning");
                }
            })
            .catch(error => {
                console.error('Erro:', error);
                showToast("Erro ao pesquisar a obra.", "danger");
            });
    });
});

function salvarObra(button) {
    const obraData = decodeURIComponent(button.getAttribute('data-obra'));

    if (!obraData || obraData.trim() === '') {
        showToast("Dados da obra não encontrados.", "warning");
        return;
    }

    try {
        const obra = JSON.parse(obraData);

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
                    showToast("Obra salva com sucesso!", "success");
                } else {
                    showToast("Erro ao salvar a obra.", "danger");
                }
            })
            .catch(error => {
                console.error('Erro:', error);
                showToast("Erro ao salvar a obra.", "danger");
            });
    } catch (error) {
        console.error("Erro ao parsear o JSON:", error);
        showToast("Erro ao salvar a obra.", "danger");
    }
}

function showToast(msg, type = 'info') {
    const containerToast = document.getElementById('containerToast');

    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${msg}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;

    containerToast.appendChild(toast);

    const bsToast = new bootstrap.Toast(toast, { delay: 3000 }); 
    bsToast.show();

    toast.addEventListener('hidden.bs.toast', () => {
        toast.remove();
    });
}
