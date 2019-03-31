function setClocks () {
  const norm = i => i < 10 ? `0${i}` : i
  const today = new Date()
  const hour = norm(today.getHours())
  const mins = norm(today.getMinutes())
  const clocks = document.querySelectorAll('.clock')

  for (const clock of clocks) {
    clock.innerHTML = `${hour}:${mins}`
  }

  setTimeout(setClocks, (60 - today.getSeconds()) * 1000)
}

let history = []

function showScreen (name) {
  if (name === '--back') {
    history.pop()
    name = history[history.length - 1]
  } else {
    history.push(name)
  }

  const screens = document.querySelectorAll('.screen')
  let currentScreen = null

  for (const screen of screens) {
    if (screen.id === name) {
      currentScreen = screen
      screen.classList.add('active')
    } else {
      screen.classList.remove('active')
    }
  }

  if (currentScreen.dataset.call) {
    window[currentScreen.dataset.call](currentScreen)
  }

  if (name === 'lockscreen') {
    document.querySelector('#status .clock').style.opacity = 0
    document.querySelector('#backbar').style.display = 'none'
  } else {
    document.querySelector('#status .clock').style.opacity = 1
    document.querySelector('#backbar').style.display = ''
    document.querySelector('#current-screen-name').innerHTML = currentScreen.dataset.name
  }

  if (name === 'mainmenu') {
    document.querySelector('#only-mainmenu').style.display = ''
    document.querySelector('#not-mainmenu').style.display = 'none'
  } else {
    document.querySelector('#not-mainmenu').style.display = ''
    document.querySelector('#only-mainmenu').style.display = 'none'
  }
}

function updatePeople (screen) {
  const people = window.data.people.sort((p1, p2) => p1.distance - p2.distance)
  screen.innerHTML = ''

  for (const { name, distance, picture } of people) {
    let el = document.createElement('div')
    el.classList.add('person')
    el.innerHTML = `<div>
        <img src="assets/people/${picture}">
      </div>
      <div>
        <p>${name}</p>
        <p><i class="fas fa-ruler"></i> ${distance}m</p>
      </div>
    </div>`

    el.addEventListener('click', () => {
      window.alert('Não implementado: visitar ' + name)
    })

    screen.appendChild(el)
  }
}

function startup () {
  setClocks()

  document.querySelectorAll('.goto').forEach(el => {
    el.addEventListener('click', event => {
      event.preventDefault()
      showScreen(el.dataset.to)
    })
  })

  showScreen('lockscreen')
}

startup()
