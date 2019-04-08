var currentBudget = window.data.currentBudget

var budgets = null

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

function updateScreenName (name) {
  document.querySelector('#current-screen-name').innerHTML = name
}

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
    document.querySelector('#only-mainmenu').style.display = ''
    document.querySelector('#not-mainmenu').style.display = 'none'
  } else {
    document.querySelector('#not-mainmenu').style.display = ''
    document.querySelector('#only-mainmenu').style.display = 'none'
  }
}

function turnOffFlashLight () {
  document.body.classList.remove('flashlight')
}

function turnOnFlashLight (screen) {
  document.body.classList.add('flashlight')
}

function distanceSort (a, b) {
  return a.distance - b.distance
}

function updatePeople (screen) {
  const people = window.data.people.sort(distanceSort)
  screen.innerHTML = ''

  for (const { name, distance, picture } of people) {
    let el = document.createElement('div')
    el.classList.add('item')
    el.innerHTML = `<div>
        <img src="assets/people/${picture}">
      </div>
      <div>
        <p>${name}</p>
        <p><i class="fas fa-ruler"></i> ${distance}m</p>
      </div>`

    el.addEventListener('click', () => {
      window.alert('Não implementado: visitar ' + name)
    })

    screen.appendChild(el)
  }
}

const placesIcons = {
  restaurants: 'utensils',
  parks: 'tree',
  monuments: 'landmark',
  markets: 'store',
  diversions: 'chess'
}

function getPlaces (kind) {
  if (kind === 'recommended') {
    window.alert('Not implemented')
    // TODO: pick random
    return []
  }

  if (kind === 'favourites') {
    return window.data.favourites
  }

  return window.data.places[kind].sort(distanceSort).map(place => {
    place.kind = kind
    return place
  })
}

function updatePlaces (screen, kind, title) {
  const places = getPlaces(kind)
  screen.innerHTML = ''

  updateScreenName(title)

  for (const { name, distance, rating, kind } of places) {
    let el = document.createElement('div')
    el.classList.add('item')
    el.dataset.args = `${name}#${distance}#${rating}`
    el.innerHTML = `<div>
        <i class="${kind} fas fa-${placesIcons[kind]}"></i>
      </div>
      <div>
        <p>${name}</p>
        <p><i class="fas fa-ruler"></i> ${distance}m</p>
      </div>`

    el.addEventListener('click', () => {
      showScreen('place-desc', el)
    })

    screen.appendChild(el)
  }
}

function updatePlaceInfo (screen, name, distance, rating) {
  updateScreenName(name)

  // TODO fazer o menu c/ classificaç\ao, distancia, numero de pessoas e possibilidade de encaminhar pro GPS
  // se for restaurante meter botao reservar.

  console.log(name, distance, rating)
}

function getBudgets () {
  return window.data.budgets.sort((a, b) => {
    if (a.active && !b.active) return 1
    if (b.active && !a.active) return 1
    if (a.date > b.date) return -1
    if (b.date > a.date) return 1
    return 0
  })
}

function createBudget () {
  console.log(document.getElementById('new-budget-name').value)
  console.log(document.getElementById('new-budget-value').value)

  currentBudget = {
    name: document.getElementById('new-budget-name').value,
    date: new Date(2019, 4),
    budget: document.getElementById('new-budget-value').value,
    expenses: [
      {
        name: 'Restaurante A Ribeira',
        value: 50
      },
      {
        name: 'Templo de Diana',
        value: 30
      },
      {
        name: 'Bela Vista',
        value: 2220
      }
    ]
  }
}

function deleteBudget () {
  currentBudget = null
}

function endBudget () {
  budgets.push(currentBudget)
  currentBudget = null
}

function getNewBudgetEl () {
  let el = document.createElement('div')
  el.classList.add('item')
  el.innerHTML = `<div>
      <i class="recommended fas fa-plus"></i>
    </div>
    <div>
      <p>Novo Orçamento</p>
      <p></p>
    </div>`

  el.addEventListener('click', () => {
    showScreen('create-budget', el)
  })

  return el
}

function getNewExpense () {
  let el = document.createElement('div')
  el.classList.add('item')
  el.innerHTML = `<div>
      <i class="recommended fas fa-plus"></i>
    </div>
    <div>
      <p>Nova Despesa</p>
      <p></p>
    </div>`

  el.addEventListener('click', () => {
    showScreen('create-expense', el)
  })

  return el
}

function getTextDate (date) {
  const year = date.getFullYear()
  const month = date.getMonth() < 10 ? `0${date.getMonth()}` : date.getMonth()
  return `${year} - ${month}`
}

function updateBudget (screen) {
  budgets = getBudgets()
  console.log(currentBudget)

  screen.innerHTML = ''
  if (currentBudget == null) {
    screen.appendChild(getNewBudgetEl())
  } else {
    let el = document.createElement('div')
    el.classList.add('item')
    el.dataset.args = `${currentBudget.name}`
    el.innerHTML = `<div>
        <i class="fas fa-plane" data-to="budget-desc" data-args="${el.dataset.args}"></i>
      </div>
      <div>
        <p>${currentBudget.name}</p>
        <p><i class="far fa-calendar-alt"></i> ${getTextDate(currentBudget.date)}</p>
        <t>ATIVO</t>
      </div>`

    el.addEventListener('click', () => {
      showScreen('current-budget-desc', el)
    })

    screen.appendChild(el)
  }

  for (const { name, date, budget } of budgets) {
    let el = document.createElement('div')
    el.classList.add('item')
    el.dataset.args = `${name}#${budget}`
    el.innerHTML = `<div>
        <i class="fas fa-plane" data-to="budget-desc" data-args="${name}"></i>
      </div>
      <div>
        <p>${name}</p>
        <p><i class="far fa-calendar-alt"></i> ${getTextDate(date)}</p>
      </div>`

    el.addEventListener('click', () => {
      showScreen('budget-desc', el)
    })

    screen.appendChild(el)
  }

  console.log(budgets)
}

function showCurrentBudget (screen, title) {
  const expenses = currentBudget.expenses
  console.log(expenses)

  updateScreenName(title)

  screen.innerHTML = `<div id=fixed><text>Valor Disponível: ${currentBudget.budget}<i class="fas fa-euro-sign"></i></text></div><div id= no-over>`
  spent = 0

  screen.appendChild(getNewExpense())
  for (const { name, value } of expenses) {
    let el = document.createElement('div')
    el.classList.add('expense')
    el.innerHTML = `
      <div>
        <p>${name}</p>
        <p>Despesa: ${value}<i class="fas fa-euro-sign"></i></p>
      </div>`

    spent += value

    el.addEventListener('click', () => {
      window.alert('Go to place')
    })

    screen.appendChild(el)
  }
  let el = document.createElement('div')
  if (spent > currentBudget.budget) {
    el.classList.add('budget-excess')
  } else {
    el.classList.add('budget-normal')
  }
  el.innerHTML = `<p>Valor Total Gasto: ${spent}<i class="fas fa-euro-sign"></i><p>
  <button id= "end-budget" onclick="endBudget()" ><i class="fas fa-book-open"></i> Concluir Viagem</button>
  <button id= "delete-budget" onclick="deleteBudget()"><i class="fas fa-book-open"></i> Apagar</button>`
  screen.appendChild(el)
}

function getExpenses (BudgetName) {
  for (const { name, expenses } of budgets) {
    if (name == BudgetName) {
      return expenses
    }
  }
}

function createExpense () {
  currentBudget.expenses.push(
    {
      name: document.getElementById('new-expense-name').value,
      value: parseInt(document.getElementById('new-expense-value').value)
    }
  )
}

function showBudget (screen, title, budget) {
  const expenses = getExpenses(title)
  console.log(expenses)

  updateScreenName(title)

  screen.innerHTML = `<div id=fixed><text>Valor Disponível: ${budget}<i class="fas fa-euro-sign"></i></text></div><div id= no-over>`
  spent = 0
  for (const { name, value } of expenses) {
    let el = document.createElement('div')
    el.classList.add('expense')
    el.innerHTML = `<div>
        <i class="fas fa-euro-sign"></i>
      </div>
      <div>
        <p>${name}</p>
        <p>Despesa: ${value}<i class="fas fa-euro-sign"></i></p>
      </div>`

    spent += value

    el.addEventListener('click', () => {
      window.alert('Go to place')
    })

    screen.appendChild(el)
  }
  let el = document.createElement('div')
  if (spent > budget) {
    el.classList.add('budget-excess')
  } else {
    el.classList.add('budget-normal')
  }
  el.innerHTML = `<p>Valor Total Gasto: ${spent}<i class="fas fa-euro-sign"></i><p>`
  screen.appendChild(el)
}

function startup () {
  setClocks()

  document.querySelectorAll('.goto').forEach(el => {
    el.addEventListener('click', event => {
      event.preventDefault()

      if (el.dataset.needsunlock) {
        if (!history.find(el => el.name === 'mainmenu')) return
      }

      showScreen(el.dataset.to, el)
    })
  })

  showScreen('lockscreen')
}

startup()
