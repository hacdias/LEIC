document.querySelectorAll('#people > div').forEach(el => {
  el.addEventListener('click', () => {
    el.classList.toggle('nyan')
  })
})
