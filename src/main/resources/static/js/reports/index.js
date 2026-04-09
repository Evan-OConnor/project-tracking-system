const searchInput = document.getElementById('clientSearch');
    const resultsList = document.getElementById('resultsList');
    const clientIdInput = document.getElementById('clientId');

    // Clear selected client ID if user types again
    searchInput.addEventListener('input', function () {
        clientIdInput.value = '';

        const query = searchInput.value.trim();

        if (query.length < 2) {
            resultsList.innerHTML = '';
            return;
        }

        fetch('/api/clients/search?query=' + encodeURIComponent(query))
            .then(response => response.json())
            .then(data => {
                resultsList.innerHTML = '';

                data.forEach(client => {
                    const li = document.createElement('li');
                    li.className = 'list-group-item list-group-item-action';
                    li.textContent = client.name;

                    li.addEventListener('click', () => {
                        searchInput.value = client.name;
                        clientIdInput.value = client.id;
                        resultsList.innerHTML = '';
                    });

                    resultsList.appendChild(li);
                });
            })
            .catch(error => {
                console.error('Error fetching clients:', error);
            });
    });

    function validateClientSelection() {
        if (!clientIdInput.value) {
            alert("Please select a client from the list.");
            return false;
        }
        return true;
    }