/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */

let history = []

function showScreen (name, el) {
  let args = (el ? (el.dataset.args || '') : '').trim().split('#')

  if (name === '--back') {
    history.pop()
    name = history[history.length - 1].name
    args = history[history.length - 1].args
  } else {
    history.push({ name, args })
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

  turnOffFlashLight()
  if (currentScreen.dataset.call) {
    window[currentScreen.dataset.call](currentScreen, ...args)
  }

  if (name === 'lockscreen') {
    document.querySelector('#status .clock').style.opacity = 0
    document.querySelector('#backbar').style.display = 'none'
  } else {
    document.querySelector('#status .clock').style.opacity = 1
    document.querySelector('#backbar').style.display = ''
    if (currentScreen.dataset.name) updateScreenName(currentScreen.dataset.name)
  }

  if (name === 'mainmenu') {
    document.querySelector('#only-call').style.display = 'none'
    document.querySelector('#only-mainmenu').style.display = ''
    document.querySelector('#not-mainmenu').style.display = 'none'
  } else if (name === 'call' || name === 'calling') {
    document.querySelector('#only-call').style.display = ''
    document.querySelector('#only-mainmenu').style.display = 'none'
    document.querySelector('#not-mainmenu').style.display = 'none'
  } else {
    document.querySelector('#only-call').style.display = 'none'
    document.querySelector('#not-mainmenu').style.display = ''
    document.querySelector('#only-mainmenu').style.display = 'none'
  }

  document.querySelector('#screen-space').scrollTop = 0
}

function turnOffFlashLight () {
  document.body.classList.remove('flashlight')
}

function turnOnFlashLight (screen) {
  document.body.classList.add('flashlight')
}

function runAndBack (...fns) {
  for (const fn of fns) {
    fn()
  }
  showScreen('--back')
}

function createRecommended () {
  var restaurants = window.data.places.restaurants
  var monuments = window.data.places.monuments
  var markets = window.data.places.markets
  var diversions = window.data.places.diversions
  var parks = window.data.places.parks
  var all = restaurants.concat(monuments, markets, diversions, parks)

  var recommended = []
  var random = Math.floor(Math.random() * (all.length - 9)) + 3

  i = 0
  while (i < random) {
    var n = Math.floor(Math.random() * all.length)
    recommended.push(all[n])
    all.splice(n, 1)
    i++
  }

  window.data.recommended = recommended
}

function startup () {
  setupClock()
  setupTextKeyboard()
  setupNumericKeyboard()

  createRecommended()
  enableGoto()

  placesBootstrap()
  gpsBootstrap()
  budgetBootstrap()
  bootstrapPeople()
  peopleBootstrap()

  showScreen('lockscreen')

  document.body.classList.remove('dn')
}

startup()
