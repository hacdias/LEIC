document.querySelectorAll('#people > div').forEach(el => {
  el.addEventListener('click', () => {
    el.classList.toggle('nyan')

    const musicEl = document.getElementById('music')
    if (document.querySelectorAll('.nyan').length > 0) {
      musicEl.play()
    } else {
      musicEl.pause()
    }
  })
})
