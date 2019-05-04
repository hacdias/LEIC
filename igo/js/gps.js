/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */

let images = []

function preloadImages () {
  for (let i = 1; i <= 16; i++) {
    let m = new Image()
    m.src = `./assets/maps/${i}.png`
    images.push(m)

    for (let j = 0; j <= 10; j++) {
      let p = new Image()
      p.src = `./assets/maps/${i}-${j}.png`
      images.push(p)
    }
  }
}

function showGpsPath (screen, id) {
  const place = getPlace(id)

  screen.style.backgroundImage = `url(./assets/maps/${place.map}.png)`
  screen.querySelector('button').dataset.args = id
}

function startGpsRoute (screen, id) {
  const place = getPlace(id)

  let i = 0
  screen.style.backgroundImage = `url(./assets/maps/${place.map}.png)`
  screen.querySelector('button').classList.add('dn')

  const update = () => {
    if (i < 10) {
      screen.style.backgroundImage = `url(./assets/maps/${place.map}-${++i}.png)`
    } else {
      screen.querySelector('button').classList.remove('dn')
      stop()
    }
  }

  const interval = setInterval(update, 1000)

  const stop = () => {
    clearInterval(interval)
    runAndBack()
    document.querySelector('[data-to=--back]').removeEventListener('click', stop)
  }

  document.querySelector('[data-to=--back]').addEventListener('click', stop)
}

function updateGpsResults (el) {
  const value = el.value.toLowerCase()

  let res = document.querySelector('.gps-results')
  res.innerHTML = ''

  for (const key in window.data.places) {
    for (const { name, id } of window.data.places[key]) {
      if (name.toLowerCase().includes(value)) {
        let li = document.createElement('li')
        li.dataset.args = id
        li.innerHTML = name
        li.addEventListener('click', () => {
          showScreen('gps-path', li)
        })
        res.appendChild(li)
      }
    }
  }
}

function gpsBootstrap () {
  preloadImages()
  enableKeybaordFor(document.getElementById('gps-input'))
}
