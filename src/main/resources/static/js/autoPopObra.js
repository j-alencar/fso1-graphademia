document.addEventListener('DOMContentLoaded', function () {
    const botaoAutoPop = document.getElementById('autoPopulateBtn');

    botaoAutoPop.addEventListener('click', function () {
        const obraId = botaoAutoPop.getAttribute('data-obra-id');
        autoPopular(obraId); 
    });
});

function autoPopular(obraId) {
    fetch(`/obras/${obraId}/autopopular`, {
        method: 'GET'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Resposta da rede não foi OK');
        }
    })
    .then(data => {
        if (data.status === 'ok') {
            alert("Campos de obra populados!");
            console.log("Atualizei a obra:", data.data);
            updateFormFields(data.data);
        } else {
            alert("Erro populando Obra: " + data.message);
        }
    })
    .catch(error => {
        console.error("Error durante autoPopObra:", error);
        alert("Falha ao popular campos da Obra.");
    });
}

function updateFormFields(obra) {
    document.getElementById('title').value = obra.title || '';
    document.getElementById('year').value = obra.year || '';
    document.getElementById('doi').value = obra.doi || '';
    document.getElementById('publicationVenueName').value = obra.publicationVenueName || '';
    document.getElementById('publicationVenueType').value = obra.publicationVenueType || '';
    document.getElementById('url').value = obra.url || '';
    document.getElementById('tldr').value = obra.tldr || '';
}
