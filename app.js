document.querySelectorAll('.profile').forEach(el => {
  el.addEventListener('click', () => {
    el.classList.toggle('rotate')
  })
})
