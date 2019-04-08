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

function numberWithSpaces (x) {
  var parts = x.toString().split('.')
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ' ')
  return parts.join('.')
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

function runAndBack (...fns) {
  for (const fn of fns) {
    fn()
  }

  showScreen('--back')
}

function clearBudget () {
  document.getElementById('new-budget-name').value = ''
  document.getElementById('new-budget-value').value = ''
  document.getElementById('new-budget-submit').classList.add('disabled')
}

function validateNewBudget () {
  let name = document.getElementById('new-budget-name').value
  let value = parseInt(document.getElementById('new-budget-value').value)

  if (name === '' || isNaN(value)) {
    // this shouldn't happen, but we never know!
    return
  }

  if (value <= 0) {
    return
  }

  return { name, value }
}

function onNewBudgetChange () {
  if (validateNewBudget()) {
    document.getElementById('new-budget-submit').classList.remove('disabled')
  } else {
    document.getElementById('new-budget-submit').classList.add('disabled')
  }
}

function confirmationBox ({
  question,
  leftMsg = 'Não',
  rightMsg = 'Sim',
  leftClass = '',
  rightClass = 'ok',
  leftHandler,
  rightHandler,
  anywaysHandler
}) {
  const overlay = document.getElementById('overlay')
  const box = document.getElementById('confirmation-box')
  const leftButton = box.querySelector('#left-confirm')
  const rightButton = box.querySelector('#right-confirm')

  overlay.classList.add('active')
  box.classList.add('active')

  box.querySelector('p').innerHTML = question
  leftButton.innerHTML = leftMsg
  rightButton.innerHTML = rightMsg
  leftButton.className = leftClass
  rightButton.className = rightClass

  const reset = () => {
    leftButton.removeEventListener('click', leftAction)
    rightButton.removeEventListener('click', rightAction)
    overlay.classList.remove('active')
    box.classList.remove('active')
  }

  const leftAction = () => {
    if (leftHandler) leftHandler()
    if (anywaysHandler) anywaysHandler()
    reset()
  }

  const rightAction = () => {
    if (rightHandler) rightHandler()
    if (anywaysHandler) anywaysHandler()
    reset()
  }

  leftButton.addEventListener('click', leftAction)
  rightButton.addEventListener('click', rightAction)
}

function createBudget () {
  if (window.data.currentBudget) {
    // this shouldn't happen, but we never know!
    return
  }

  let data = validateNewBudget()
  if (!data) {
    // this shouldn't happen, but we never know!
    return
  }

  confirmationBox({
    question: `Confirma a criação de um orçamento de valor ${data.value} €?`,
    rightHandler: () => {
      window.data.currentBudget = {
        id: uuidv4(),
        name: data.name,
        budget: data.value,
        date: new Date(),
        expenses: []
      }

      runAndBack(clearBudget)
    }
  })
}

function deleteBudget () {
  confirmationBox({
    question: `Deseja remover o orçamento atual?`,
    rightClass: 'cancel',
    rightHandler: () => {
      window.data.currentBudget = null
      runAndBack()
    }
  })
}

function endBudget () {
  confirmationBox({
    question: `Deseja terminar a viagem atual?`,
    rightHandler: () => {
      window.data.budgets.push(window.data.currentBudget)
      window.data.currentBudget = null
      runAndBack()
    }
  })
}

function getTextDate (date) {
  const year = date.getFullYear()
  const month = date.getMonth() < 10 ? `0${date.getMonth()}` : date.getMonth()
  return `${year} - ${month}`
}

function getBudgetElement ({ id, name, date }, current = false) {
  let el = document.createElement('div')
  el.classList.add('item')
  if (current) el.classList.add('current')
  el.dataset.args = `${id}#${current}`
  el.innerHTML = `<div>
      <i class="fas fa-plane"></i>
    </div>
    <div>
      <p>${name}</p>
      <p><i class="far fa-calendar-alt"></i> ${getTextDate(date)}</p>
    </div>`

  el.addEventListener('click', () => {
    showScreen('budget-desc', el)
  })

  return el
}

function listBudgets (screen) {
  let budgets = getBudgets()
  console.log(window.data.currentBudget)

  let budgetsDiv = screen.querySelector('#budget-list')

  budgetsDiv.innerHTML = ''

  if (window.data.currentBudget === null) {
    document.getElementById('new-budget-button').classList.remove('dn')
  } else {
    document.getElementById('new-budget-button').classList.add('dn')
    budgetsDiv.appendChild(getBudgetElement(window.data.currentBudget, true))
  }

  for (const budget of budgets) {
    budgetsDiv.appendChild(getBudgetElement(budget))
  }

  console.log(budgets)
}

function clearExpense () {
  document.getElementById('new-expense-name').value = ''
  document.getElementById('new-expense-value').value = ''
  document.getElementById('new-expense-submit').classList.add('disabled')
}

function validateNewExpense () {
  let name = document.getElementById('new-expense-name').value
  let value = parseInt(document.getElementById('new-expense-value').value)

  if (name === '' || isNaN(value)) {
    // this shouldn't happen, but we never know!
    return undefined
  }

  return { name, value }
}

function onNewExpenseChange () {
  if (validateNewExpense()) {
    document.getElementById('new-expense-submit').classList.remove('disabled')
  } else {
    document.getElementById('new-expense-submit').classList.add('disabled')
  }
}

function createExpense () {
  let data = validateNewExpense()
  if (!data) {
    // shouldn't happen
    return
  }

  const spent = window.data.currentBudget.expenses.reduce((acc, e) => acc + e.value, 0)
  const overload = spent + data.value > window.data.currentBudget.budget

  let question = `Deseja adicionar uma despesa no valor de ${data.value} €?`
  if (overload) {
    question += ` Irá exceder o orçamento definido de ${window.data.currentBudget.budget}`
  }

  confirmationBox({
    question: question,
    rightClass: overload ? 'cancel' : 'ok',
    rightHandler: () => {
      window.data.currentBudget.expenses.push({ ...data })
      runAndBack(clearExpense)
    }
  })
}

function getBudget (id) {
  if (window.data.currentBudget && window.data.currentBudget.id === id) {
    return window.data.currentBudget
  }

  for (const budget of window.data.budgets) {
    if (budget.id === id) return budget
  }
}

function showBudget (screen, id) {
  const { name, budget, expenses } = getBudget(id)
  const isCurrent = window.data.currentBudget && window.data.currentBudget.id === id

  screen.querySelectorAll('.only-current').forEach(el => {
    if (isCurrent) el.classList.remove('dn')
    else el.classList.add('dn')
  })

  updateScreenName(name)

  const expensesDiv = screen.querySelector('#budget-expenses')
  expensesDiv.innerHTML = ''

  let spent = 0
  for (const { name, value } of expenses) {
    let el = document.createElement('div')
    el.classList.add('expense')
    el.innerHTML = `<div>
        <p>${name}</p>
        <p>Despesa: ${numberWithSpaces(value)} €</p>
      </div>`

    spent += value
    expensesDiv.appendChild(el)
  }

  screen.querySelector('#budget-bar #budget-spent').innerHTML = numberWithSpaces(spent)
  screen.querySelector('#budget-bar #budget-max').innerHTML = numberWithSpaces(budget)

  if (spent > budget) {
    screen.querySelector('#budget-bar div').classList.add('excess')
  } else {
    screen.querySelector('#budget-bar div').classList.remove('excess')
    screen.querySelector('#budget-bar div').style.width = `${spent / budget * 100}%`
  }
}

function contactless () {
  const what = window.prompt('Insira o Vendedor')
  if (!what) return
  const howMuch = Number(window.prompt('Insira o Valor da Compra'))
  if (isNaN(howMuch)) return

  confirmationBox({
    question: `Confirma a compra no valor de ${numberWithSpaces(howMuch)} € no ${what}?`,
    rightHandler: () => {
      if (!window.data.currentBudget) {
        return
      }

      window.data.currentBudget.expenses.push({
        name: what,
        value: howMuch
      })
    }
  })
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
