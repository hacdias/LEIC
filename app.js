document.querySelectorAll('.profile').forEach(el => {
  el.addEventListener('click', () => {
    const src = el.src
    el.src = el.dataset.url
    el.dataset.url = src
  })
})
