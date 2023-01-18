import { withRouter } from 'storybook-addon-react-router-v6'
import { Navbar } from '../components'

export default {
  title: 'Navbar',
  component: Navbar,
  decorators: [withRouter]
}

// @ts-ignore
const Template = (args) => <Navbar {...args} />
export const Primary = Template.bind({})
// @ts-ignore
Primary.args = {
  sites: [{ name: 'Users', path: '/users', icon: 'users' }, { name: 'Groups', path: '/groups', icon: 'groups' }],
  currentPath: '/users',
  toolName: 'User Management',
  loggedInUser: {
    name: 'testadmin'
  }
}
