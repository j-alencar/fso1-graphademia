function deletarObra(id) {
    if (confirm("Tem certeza que deseja deletar esta obra?")) {
        fetch(`/obras/${id}`, {
            method: 'DELETE',
        })
        .then(response => {
            if (response.ok) {
                alert("Obra deletada com sucesso!");
                location.reload();
            } else {
                response.text().then(errorMsg => alert(errorMsg));
            }
        })
        .catch(error => console.error("Erro ao deletar obra:", error));
    }
}
